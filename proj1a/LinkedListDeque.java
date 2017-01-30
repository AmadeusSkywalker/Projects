public class LinkedListDeque<Type>{

   private class StuffNode{
     public Type item;
     public StuffNode next;
     public StuffNode prev;
     public StuffNode(Type c,StuffNode a,StuffNode b){
       item=c;
       next=a;
       prev=b;
     }
   }

   private StuffNode Sentinel;
   private int size;

   public LinkedListDeque(){
     Sentinel=new StuffNode(null,Sentinel,Sentinel);
     size=0;
   }

   public LinkedListDeque(Type x){
     Sentinel.next=new StuffNode(x,Sentinel,Sentinel);
     Sentinel.prev=Sentinel.next;
     size=1;
   }

   public void addFirst(Type x){
     if (size==0){
       Sentinel.next=new StuffNode(x,Sentinel,Sentinel);
       Sentinel.prev=Sentinel.next;
       size=size+1;
     }
     else {
         Sentinel.next=new StuffNode(x,Sentinel.next,Sentinel);
         Sentinel.next.next.prev=Sentinel.next;
         size+=1;
     }
   }

   public void addLast(Type x){
       if (size==0){
           Sentinel.next=new StuffNode(x,Sentinel,Sentinel);
           Sentinel.prev=Sentinel.next;
           size=size+1;
       }
       else{
           Sentinel.prev=new StuffNode(x,Sentinel,Sentinel.prev);
           Sentinel.prev.prev.next=Sentinel.prev;
           size+=1;
       }
   }

   public boolean isEmpty(){
       return size==0;
   }

   public int size(){
       return size;
   }

   public void printDeque(){
       int index=1;
       StuffNode pointer=Sentinel.next;
       while (index<=size){
           System.out.print(pointer.item+" ");
           index=index+1;
           pointer=pointer.next;
       }
       System.out.println();
   }

   public Type removeFirst(){
       if (size==0){
           return null;
       }
       StuffNode pointer=Sentinel.next;
       Sentinel.next=Sentinel.next.next;
       size=size-1;
       return pointer.item;
   }

   public Type removeLast(){
       if (size==0){
           return null;
       }
       StuffNode pointer=Sentinel.prev;
       Sentinel.prev=Sentinel.prev.prev;
       size=size-1;
       return pointer.item;
   }

   public Type get(int x){
       StuffNode pointer=Sentinel.next;
       if (x>=size){
           return null;
       }
       int index=x;
       while(index>0){
           pointer=pointer.next;
           index=index-1;
       }
       return pointer.item;
   }

   public Type getRecursive(int x){
       if (x>=size){
           return null;
       }
       return recursehelp(x,Sentinel.next);
   }

   public Type recursehelp(int x, StuffNode m){
       if (x==0){
           return m.item;
       }
       return recursehelp(x-1,m.next);
   }

}
