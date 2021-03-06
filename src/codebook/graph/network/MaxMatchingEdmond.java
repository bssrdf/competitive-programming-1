/*
 * The blossom algorithm (Edmonds's algorithm) finds the maximal matching for general graphs by contracting blossoms (odd-length cycles) into a single vertex.
 * 
 * Time complexity: O(V^4)
 */

package codebook.graph.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.StringTokenizer;

public class MaxMatchingEdmond {

  static BufferedReader br;
  static PrintWriter out;
  static StringTokenizer st;

  static int n, m;
  static ArrayList<ArrayList<Integer>> adj = new ArrayList<ArrayList<Integer>>();
  static boolean[] mark, used;
  static int[] match, par, id;

  public static void main (String[] args) throws IOException {
    br = new BufferedReader(new InputStreamReader(System.in));
    out = new PrintWriter(new OutputStreamWriter(System.out));
    //br = new BufferedReader(new FileReader("in.txt"));
    //out = new PrintWriter(new FileWriter("out.txt"));

    n = readInt();
    m = readInt();

    for (int i = 0; i < n; i++)
      adj.add(new ArrayList<Integer>());

    for (int j = 0; j < n; j++) {
      int a = readInt() - 1;
      int b = readInt() - 1;
      adj.get(a).add(b);
      adj.get(b).add(a);
    }
    out.println(getMaxMatching());
    out.close();
  }

  static int getMaxMatching () {
    match = new int[n];
    par = new int[n];
    id = new int[n];
    used = new boolean[n];

    Arrays.fill(match, -1);

    for (int i = 0; i < n; i++) {
      // if it hasn't already been matched, we search to see if we can augment it
      if (match[i] == -1) {
        int v = getAugmentingPath(i);
        while (v != -1) {
          int pv = par[v];
          int ppv = match[pv];
          match[v] = pv;
          match[pv] = v;
          v = ppv;
        }
      }
    }
    int res = 0;
    for (int i = 0; i < n; i++)
      if (match[i] != -1)
        res++;
    return res / 2;
  }

  static int getAugmentingPath (int src) {
    Arrays.fill(par, -1);
    used = new boolean[n];
    for (int i = 0; i < n; i++)
      id[i] = i;
    used[src] = true;
    Queue<Integer> q = new ArrayDeque<Integer>();
    q.offer(src);
    while (!q.isEmpty()) {
      int curr = q.poll();
      for (int next : adj.get(curr)) {
        // If the next node is in the same BFS tree or has already been matched, then we can skip it
        if (id[curr] == id[next] || match[curr] == next)
          continue;
        // We found a blossom, or we need to modify an existing blossom
        if (next == src || (match[next] != -1 && par[match[next]] != -1)) {
          int newBase = lca(curr, next);
          boolean[] blossom = new boolean[n];
          markPath(blossom, curr, newBase, next);
          markPath(blossom, next, newBase, curr);
          // changing the base of the blossom because the edges of the blossom are modified
          for (int i = 0; i < n; i++) {
            if (blossom[id[i]]) {
              id[i] = newBase;
              if (!used[i]) {
                used[i] = true;
                q.offer(i);
              }
            }
          }
        }
        // augmenting path found
        else if (par[next] == -1) {
          par[next] = curr;
          if (match[next] == -1)
            return next;
          next = match[next];
          used[next] = true;
          q.offer(next);
        }
      }
    }
    return -1;
  }

  // Auxiliary function that marks the blossom
  static void markPath (boolean[] blossom, int i, int b, int j) {
    for (; id[i] != b; i = par[match[i]]) {
      blossom[id[i]] = blossom[id[match[i]]] = true;
      par[i] = j;
      j = match[i];
    }
  }

  // Auxiliary function that finds the lca in the BFS tree
  static int lca (int i, int j) {
    boolean[] v = new boolean[n];
    while (true) {
      i = id[i];
      used[i] = true;
      if (match[i] == -1)
        break;
      i = par[match[i]];
    }
    while (true) {
      j = id[j];
      if (v[j])
        return j;
      j = par[match[j]];
    }
  }

  static String next () throws IOException {
    while (st == null || !st.hasMoreTokens())
      st = new StringTokenizer(br.readLine().trim());
    return st.nextToken();
  }

  static long readLong () throws IOException {
    return Long.parseLong(next());
  }

  static int readInt () throws IOException {
    return Integer.parseInt(next());
  }

  static double readDouble () throws IOException {
    return Double.parseDouble(next());
  }

  static char readCharacter () throws IOException {
    return next().charAt(0);
  }

  static String readLine () throws IOException {
    return br.readLine().trim();
  }
}
