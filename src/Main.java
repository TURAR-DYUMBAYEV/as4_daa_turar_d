import java.io.*;import java.nio.charset.StandardCharsets;import java.util.*;
public final class Main{
 @SuppressWarnings("unchecked")static Object h(Map<String,Object>x){return switch((String)x.get("task")){case"ping"->Map.of("status","ready");case"lcs"->Algorithms.lcs(x);case"merge"->Algorithms.merge(x);case"tree"->Algorithms.tree(x);case"naive"->Algorithms.naive(x);case"kmp"->Algorithms.kmp(x);case"rk"->Algorithms.rk(x);case"suffix"->Algorithms.suffix(x);case"aho"->Algorithms.aho(x);default->throw new IllegalArgumentException("Unknown task");};}
 @SuppressWarnings("unchecked")public static void main(String[]a)throws Exception{BufferedReader r=new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8));String l;while((l=r.readLine())!=null)try{System.out.println(Json.write(h((Map<String,Object>)Json.parse(l))));}catch(UnsupportedOperationException e){System.out.println(Json.write(Map.of("error",e.getMessage())));}}
}
