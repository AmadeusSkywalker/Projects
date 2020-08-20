#include <ctype.h>
#include <errno.h>
#include <fcntl.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <signal.h>
#include <sys/wait.h>
#include <termios.h>
#include <unistd.h>

#include "tokenizer.h"

/* Convenience macro to silence compiler warnings about unused function parameters. */
#define PATH_MAX        4096
#define unused __attribute__((unused))


/* Whether the shell is connected to an actual terminal or not. */
bool shell_is_interactive;

/* File descriptor for the shell input */
int shell_terminal;

/* Terminal mode settings for the shell */
struct termios shell_tmodes;

/* Process group id for the shell */
pid_t shell_pgid;

int cmd_exit(struct tokens *tokens);
int cmd_help(struct tokens *tokens);
int cmd_pwd(struct tokens *tokens);
int cmd_cd(struct tokens *tokens);

int execute_program(unused struct tokens *tokens);
char *parse_path(char *argument);
int execute_pipes(unused struct tokens *tokens);

/* Built-in command functions take token array (see parse.h) and return int */
typedef int cmd_fun_t(struct tokens *tokens);

/* Built-in command struct and lookup table */
typedef struct fun_desc {
  cmd_fun_t *fun;
  char *cmd;
  char *doc;
} fun_desc_t;

fun_desc_t cmd_table[] = {
  {cmd_help, "?", "show this help menu"},
  {cmd_exit, "exit", "exit the command shell"},
  {cmd_pwd, "pwd", "get the current directory"},
  {cmd_cd, "cd", "enter the directory"},
};

/* Prints a helpful description for the given command */
int cmd_help(unused struct tokens *tokens) {
  for (unsigned int i = 0; i < sizeof(cmd_table) / sizeof(fun_desc_t); i++)
    printf("%s - %s\n", cmd_table[i].cmd, cmd_table[i].doc);
  return 1;
}

/* Exits this shell */
int cmd_exit(unused struct tokens *tokens) {
  exit(0);
}

int cmd_pwd(unused struct tokens *tokens){
  char cwd[PATH_MAX - 1];
   if (getcwd(cwd, sizeof(cwd)) != NULL) {
       printf("%s\n", cwd);
   } else {
       perror("getcwd() error");
       return 1;
   }
   return 0;
}

int cmd_cd(unused struct tokens *tokens){
   return chdir(tokens_get_token(tokens, 1));
}

int execute_program(unused struct tokens *tokens){
  //printf("OMG1!");
  pid_t pid = fork();
  if(pid == 0){
     //printf("OMG!");
     setpgrp();
     setpgid(pid, pid);
     tcsetpgrp(0, getpgrp());
     struct sigaction act;
     act.sa_handler = SIG_DFL;
     sigaction(SIGINT, &act, NULL);
     sigaction(SIGQUIT, &act, NULL);
     sigaction(SIGTSTP, &act, NULL);
     sigaction(SIGCONT, &act, NULL);
     sigaction(SIGTTIN, &act, NULL);
     sigaction(SIGTTOU, &act, NULL);
  
     int tokens_length = tokens_get_length(tokens);
     if(tokens_length >= 3 && strcmp(tokens_get_token(tokens, tokens_length - 2), "<") == 0){
         int file_desc = open(tokens_get_token(tokens, tokens_length - 1), O_RDONLY);
         dup2(file_desc, 0);
         close(file_desc);
         tokens_length -= 2;
     }else if(tokens_length >= 3 && strcmp(tokens_get_token(tokens, tokens_length - 2), ">") == 0){
         int file_desc = open(tokens_get_token(tokens, tokens_length - 1), O_RDWR | O_CREAT | O_EXCL, 0666);
         dup2(file_desc, 1);
         close(file_desc);
         tokens_length -= 2;
     }
     char **arguments = (char**)malloc(sizeof(char*) * (tokens_length + 1));
     if (arguments == NULL){
       perror("Malloc fails");
       return 1;
     }
     int i;
     int numpipes = 0;
     int firstpipe = 0;
     for(i = 0; i < tokens_length; i++){
        arguments[i] = tokens_get_token(tokens, i);
        if(strcmp(arguments[i], "|") == 0){
            numpipes += 1;
            firstpipe = i;
        }
     }
     if(numpipes == 1){
        //for the case when there is a pipe
        char **argsfirst = (char**)malloc(sizeof(char*) * (firstpipe + 1));
        int index;
        for(index = 0; index < firstpipe; index++){
           argsfirst[index] = arguments[index];
        }
        argsfirst[firstpipe] = NULL;
        int pipe1[2];
        if (pipe(pipe1) == -1) {
            perror("bad pipe1");
            exit(1);
        }
        if ((pid = fork()) == -1) {
            perror("bad fork1");
            exit(1);
        } else if (pid == 0) {
            char *file_path = parse_path(argsfirst[0]);
            if(file_path == NULL){
                perror("Parsing fails");
                return 1;
            }
            dup2(pipe1[1], 1);
            close(pipe1[0]);
            close(pipe1[1]);
            execv(file_path, argsfirst);
        }
        else{
            wait(NULL);
        }
        char **argssecond = (char**)malloc(sizeof(char*) * (tokens_length - firstpipe));
        int j = 0;
        //int secondpipe = 0;
        for(i = firstpipe + 1; i < tokens_length; i++){
            argssecond[j] = tokens_get_token(tokens, i);
            if(strcmp(argssecond[j], "|") == 0){
                //secondpipe = i;
                break;
            }
            j += 1;
        }
        argssecond[j] = NULL;
        if ((pid = fork()) == -1) {
                perror("bad fork3");
                exit(1);
            } 
         else if (pid == 0) {
            char *file_path2 = parse_path(argssecond[0]);
            if(file_path2 == NULL){
                  perror("Parsing fails");
                  return 1;
            }
            dup2(pipe1[0], 0);
            close(pipe1[0]);
            close(pipe1[1]);
            execv(file_path2, argssecond);
                  // exec didn't work, exit
            perror("bad exec grep sbin");
            _exit(1);
            }
         else{
            wait(NULL);
         }
      }else if(numpipes == 2){
        //for the case where there are two pipes
         // I referenced the code at http://www.cs.loyola.edu/~jglenn/702/S2005/Examples/dup2.html
         int i;
         int firstindex = 0;
         int secondindex = 0;
         int pipes[4];
         pipe(pipes); 
         pipe(pipes + 2); 
         for(i = 0; i < tokens_length; i++){
              arguments[i] = tokens_get_token(tokens, i);
              if(strcmp(arguments[i], "|") == 0){
                   if(firstindex == 0){
                      firstindex = i;
                   }else{
                      secondindex = i;
                   }     
              }
          }
          char *firstargs[firstindex + 1];
          char *secondargs[secondindex - firstindex];
          char *thirdargs[tokens_length - secondindex];
          for(i = 0; i < firstindex; i++){
             firstargs[i] = tokens_get_token(tokens, i);
          }
          firstargs[firstindex] = NULL;
          int index = 0;
          for(i = firstindex + 1; i < secondindex; i++){
             secondargs[index] = tokens_get_token(tokens, i);
             index += 1;
          }
          secondargs[index] = NULL;
          index = 0;
          for(i = secondindex + 1; i < tokens_length; i++){
             thirdargs[index] = tokens_get_token(tokens, i);
             index += 1;
          }
          thirdargs[index] = NULL;
          if (fork() == 0){
                char *file_path1 = parse_path(firstargs[0]);
                if(file_path1 == NULL){
                      perror("Parsing fails");
                      return 1;
                }
                dup2(pipes[1], 1);
                close(pipes[0]);
                close(pipes[1]);
                close(pipes[2]);
                close(pipes[3]);
                execv(file_path1, firstargs);
          }else{
                if (fork() == 0){
                    char *file_path2 = parse_path(secondargs[0]);
                    if(file_path2 == NULL){
                          perror("Parsing fails");
                          return 1;
                    }
                    dup2(pipes[0], 0);
                    dup2(pipes[3], 1);
                    close(pipes[0]);
                    close(pipes[1]);
                    close(pipes[2]);
                    close(pipes[3]); 
                    execv(file_path2, secondargs);
                }else{
                    if (fork() == 0){
                       char *file_path3 = parse_path(thirdargs[0]);
                       if(file_path3 == NULL){
                          perror("Parsing fails");
                          return 1;
                       }
                       dup2(pipes[2], 0);
                       close(pipes[0]);
                       close(pipes[1]);
                       close(pipes[2]);
                       close(pipes[3]);
                       execv(file_path3, thirdargs);
                    }
                }
          }
          close(pipes[0]);
          close(pipes[1]);
          close(pipes[2]);
          close(pipes[3]);
          for (i = 0; i < 3; i++){
              wait(NULL);
          }    
      }else if(numpipes > 2){
          char **args[50];
          int i;
          int prev = 0;
          int index = 0;
          for(i = 0; i < tokens_length; i++){
              while(i < tokens_length && strcmp(tokens_get_token(tokens, i), "|") != 0 ){
                   i += 1;
              }
              char **argcurr = malloc(sizeof(char*) * (i - prev + 1));
              int currIndex = 0;
              for(int j = prev; j < i; j++){
                  argcurr[currIndex] =  tokens_get_token(tokens, j);
                  //printf("%s", argcurr[currIndex]);
                  currIndex += 1;
              }
              //printf("\n");
              argcurr[currIndex] = NULL;
              //printf("%s", argcurr[0]);
              prev = i + 1;
              args[index] = argcurr;
              index += 1;
          }
          int pipes[2 * numpipes];
          for(i = 0; i < numpipes * 2; i+=2){
             pipe(pipes + i);
          }
          for(i = 0; i < numpipes + 1; i++){
              if(i == 0 && fork() == 0){
                  char *file_path = parse_path(args[i][0]);
                  if(file_path == NULL){
                        perror("Parsing fails");
                        return 1;
                  }
                  dup2(pipes[1],1);
                  for(int k = 0; k < 2 * numpipes; k++){
                     close(pipes[k]);
                  }
                  execv(file_path, args[i]);
              }else if(i > 0 && i < numpipes && fork() == 0){
                  char *file_path = parse_path(args[i][0]);
                  if(file_path == NULL){
                        perror("Parsing fails");
                        return 1;
                  }
                  dup2(pipes[i * 2 - 2],0);
                  dup2(pipes[i * 2 + 1],1);
                  for(int k = 0; k < 2 * numpipes; k++){
                     close(pipes[k]);
                  }
                  execv(file_path, args[i]);
              }else if(i == numpipes && fork() == 0){
                  char *file_path = parse_path(args[i][0]);
                  if(file_path == NULL){
                        perror("Parsing fails");
                        return 1;
                  }
                  dup2(pipes[i * 2 - 2],0);
                  for(int k = 0; k < 2 * numpipes; k++){
                     close(pipes[k]);
                  }
                  execv(file_path, args[i]);
              }
          }
          for(int k = 0; k < 2 * numpipes; k++){
              close(pipes[k]);
          }
          for (i = 0; i < numpipes + 1; i++){
              wait(NULL);
          }   
      }else{
            //for the case when there is no pipe
            arguments[tokens_length] = NULL;
            char *file_path = parse_path(arguments[0]);
            //printf(file_path);
            if(file_path == NULL){
                perror("Parsing fails");
                return 1;
            }
            execv(file_path, arguments);
      }
  }else{
     //printf("Gidsfds");
     wait(NULL);
     setpgid(pid, pid);
     tcsetpgrp(0, getpgrp());
  }
  return 0;
}

/* Looks up the built-in command, if it exists. */
int lookup(char cmd[]) {
  //printf("%li", sizeof(cmd_table) / sizeof(fun_desc_t));
  for (unsigned int i = 0; i < sizeof(cmd_table) / sizeof(fun_desc_t); i++)
    if (cmd && (strcmp(cmd_table[i].cmd, cmd) == 0))
      return i;
    //printf(sizeof(cmd_table) / sizeof(fun_desc_t));
  return -1;
}

char *parse_path(char *argument){
  if(access(argument, X_OK) != -1){
      return argument;
  }
  char *buffer = (char *)(malloc(sizeof(char) * 500));
  if (buffer == NULL){
       perror("Malloc fails");
       return NULL;
  }
  char *path = getenv("PATH");
  const char s[2] = ":";
  char *token;
  token = strtok(path, s);
  while( token != NULL ) {
      strcpy(buffer, token);
      strcat(buffer, "/");
      strcat(buffer, argument);
      //printf(buffer);
      //printf("\n");
      if(access(buffer, X_OK) != -1){
        //printf("great!");
        return buffer;
      }
      token = strtok(NULL, s);
   }
   free(buffer);
   return NULL;
}

/* Intialization procedures for this shell */
void init_shell() {
  struct sigaction act;
  /* Our shell is connected to standard input. */
  shell_terminal = STDIN_FILENO;

  /* Check if we are running interactively */
  shell_is_interactive = isatty(shell_terminal);

  if (shell_is_interactive) {
    /* If the shell is not currently in the foreground, we must pause the shell until it becomes a
     * foreground process. We use SIGTTIN to pause the shell. When the shell gets moved to the
     * foreground, we'll receive a SIGCONT. */
    while (tcgetpgrp(shell_terminal) != (shell_pgid = getpgrp()))
      kill(-shell_pgid, SIGTTIN);

    /* Saves the shell's process id */
    shell_pgid = getpid();

    /* Take control of the terminal */
    tcsetpgrp(shell_terminal, shell_pgid);

    /* Save the current termios to a variable, so it can be restored later. */
    tcgetattr(shell_terminal, &shell_tmodes);
  }
  act.sa_handler = SIG_IGN;
  sigaction(SIGINT, &act, NULL);
  sigaction(SIGQUIT, &act, NULL);
  sigaction(SIGTSTP, &act, NULL);
  sigaction(SIGCONT, &act, NULL);
  sigaction(SIGTTIN, &act, NULL);
  sigaction(SIGTTOU, &act, NULL);
}

int main(unused int argc, unused char *argv[]) {
  init_shell();

  static char line[4096];
  int line_num = 0;

  /* Please only print shell prompts when standard input is not a tty */
  if (shell_is_interactive)
    fprintf(stdout, "%d: ", line_num);

  while (fgets(line, 4096, stdin)) {
    /* Split our line into words. */
    struct tokens *tokens = tokenize(line);
    /* Find which built-in function to run. */
    int fundex = lookup(tokens_get_token(tokens, 0));

    if (fundex >= 0) {
      cmd_table[fundex].fun(tokens);
    } else {
      /* REPLACE this to run commands as programs. */
      execute_program(tokens);
      //fprintf(stdout, "This shell doesn't know how to run programs.\n");
    }

    if (shell_is_interactive)
      /* Please only print shell prompts when standard input is not a tty */
      fprintf(stdout, "%d: ", ++line_num);

    /* Clean up memory */
    tokens_destroy(tokens);
  }

  return 0;
}
