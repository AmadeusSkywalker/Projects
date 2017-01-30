public class LinkedListDeque<Type>{

   private class StuffNode{
     public Type stuff;
     public StuffNode next;
     public StuffNode prev;
     public StuffNode(item,a,b){
       stuff=item;
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
       
     }
   }
}
