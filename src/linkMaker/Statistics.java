package linkMaker;

import java.util.ArrayList;
import java.util.List;

public class Statistics {
	 public static int factorial(int n) {
	       if (n == 0) {
	           return 1;
	       } else {
	           return n * factorial(n - 1);
	       }
	   }
	 
	 public static int getNumberOfPaths(List<List<String>> globalList) {
		 int numberOfElements=0;
		 for(List<String> list : globalList) {
			 if(list !=null) {
				 numberOfElements+=list.size();
			 }
		 }
		 return numberOfElements;
		 //return Statistics.factorial(numberOfElements);
	 }
}
