import java.util.*;

public class avion {
    public static int[] computePrefixArray(String pattern) {
		// Preprocessing step of the KMP algorithm
		int i = 1;
		int j = 0;
		int[] lps = new int[pattern.length()];
		
		while (i < pattern.length()) {
			if (pattern.charAt(i) == pattern.charAt(j)) {
				lps[i] = j + 1;
				i++;
				j++;
			}
			else {
				if (j != 0) j = lps[j-1];
				else{
					lps[i] = 0;
					i++;
				}
			}
		}
		
		return lps;
	}
	
    public static boolean kmp(String text, String pattern, int[] lps) {
		// KMP pattern searching algorithm
		int t = 0; // index of text
		int p = 0; // index of pattern
		
		while (t < text.length() && p < pattern.length()) {
			if (text.charAt(t) == pattern.charAt(p)) {
				t++;
				p++;
			}
			else {
				if (p != 0) p = lps[p-1];
				else t++;
			}
		}
		
		// if i == pattern.length(), then the pattern is found in the text
		if (p == pattern.length()) return true;
		
		return false;
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		StringBuilder result = new StringBuilder();
		// String pattern = "FBI"; // originally just to contain what patterns to look for.
		int[] lps = computePrefixArray("FBI");
		
		for (int i = 0; i < 5; i++) {
			String line = scanner.nextLine();
			
		    if (kmp(line, "FBI", lps)) {
			    result.append(i+1 + " ");
		    }
		}
		
		if (result.length() == 0) System.out.println("HE GOT AWAY!");
		else System.out.println(result.toString().trim());

		scanner.close();
	}
}
