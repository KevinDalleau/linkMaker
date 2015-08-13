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
		 int numberOfPaths=1;
		 for(List<String> list : globalList) {
			 if(list !=null && list.size() != 0) {
				 numberOfPaths*=list.size();
			 }
		 }
		 return numberOfPaths;
	 }
}
