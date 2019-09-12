package structures;

public class Node<T> { 
	 T data;	//T has same data type as the class has, generic, can be any object like int, String, etc...
	Node<T> next;	
		
	public Node(T d,Node<T> nextnode) {
		data=d;
		next=nextnode;
	} 
}
