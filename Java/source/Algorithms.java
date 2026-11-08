import java.util.*;
public final class Algorithms {

    // Shared JSON access helpers.
    static String S(Map<String, Object>m,String k) {
        return(String)m.get(k);
    }
    static int I(Object x) {
        return((Number)x).intValue();
    }
    static long L(Object x) {
        return((Number)x).longValue();
    }
    @SuppressWarnings("unchecked")
    static List<Object>A(Map<String, Object>m,String k) {
        return(List<Object>)m.get(k);
    }
    static List<Integer> bounds(int n) {
        List<Integer>r=new ArrayList<>();
        for(int i=0;i<=n;i++)r.add(i);
        return r;
    }
    // Dynamic programming table and greedy reconstruction of one LCS.
    static Map<String, Object> lcs(Map<String, Object>m) {
        String a=S(m,"a"),b=S(m,"b");
        int n=a.length(),q=b.length();
        int[][]d=new int[n+1][q+1];
        for(int i=n-1;i>=0;i--)for(int j=q-1;j>=0;j--)d[i][j]=a.charAt(i)==b.charAt(j)?d[i+1][j+1]+1:Math.max(d[i+1][j],d[i][j+1]);
        StringBuilder z=new StringBuilder();
        for(int i=0,j=0;i<n&&j<q;)if(a.charAt(i)==b.charAt(j)) {
            z.append(a.charAt(i++));
            j++;
        }
        else if(d[i+1][j]>=d[i][j+1])i++;
        else j++;
        return Map.of("length",d[0][0],"sequence",z.toString());
    }
    // Interval DP; the split table is emitted in chronological order.
    static Map<String, Object> merge(Map<String, Object>m) {
        List<Object>a=A(m,"sizes");
        int n=a.size();
        if(n<2)return Map.of("cost",0L,"plan",List.of());
        long[]p=new long[n+1];
        for(int i=0;i<n;i++)p[i+1]=p[i]+L(a.get(i));
        long[][]d=new long[n][n];
        int[][]c=new int[n][n];
        for(int z=2;z<=n;z++)for(int l=0;l+z<=n;l++) {
            int r=l+z-1;
            d[l][r]=Long.MAX_VALUE;
            for(int k=l;k<r;k++) {
                long v=d[l][k]+d[k+1][r]+p[r+1]-p[l];
                if(v<d[l][r]) {
                    d[l][r]=v;
                    c[l][r]=k;
                }
            }
        }
        List<List<Integer>>r=new ArrayList<>();
        mp(0,n-1,c,r);
        return Map.of("cost",d[0][n-1],"plan",r);
    }
    static void mp(int l,int r,int[][]c,List<List<Integer>>o) {
        if(l>=r)return;
        int k=c[l][r];
        mp(l,k,c,o);
        mp(k+1,r,c,o);
        o.add(List.of(l,k,r));
    }
    @SuppressWarnings("unchecked")
    // Iterative take/skip DP and reconstruction for a rooted tree.
    static Map<String, Object> tree(Map<String, Object>m) {
        int n=I(m.get("n"));
        if(n==0)return Map.of("weight",0L,"selected",List.of());
        List<Integer>[]g=new List[n];
        for(int i=0;i<n;i++)g[i]=new ArrayList<>();
        for(Object x:A(m,"edges")) {
            List<Object>e=(List<Object>)x;
            int u=I(e.get(0)),v=I(e.get(1));
            g[u].add(v);
            g[v].add(u);
        }
        long[]w=new long[n],t=new long[n],s=new long[n];
        List<Object>ww=A(m,"weights");
        for(int i=0;i<n;i++)w[i]=L(ww.get(i));
        int[]pa=new int[n],o=new int[n],st=new int[n];
        Arrays.fill(pa,-2);
        int top=0,k=0;
        st[top++]=0;
        pa[0]=-1;
        while(top>0) {
            int u=st[--top];
            o[k++]=u;
            for(int v:g[u])if(v!=pa[u]) {
                pa[v]=u;
                st[top++]=v;
            }
        }
        for(int x=n-1;x>=0;x--) {
            int u=o[x];
            t[u]=w[u];
            for(int v:g[u])if(pa[v]==u) {
                t[u]+=s[v];
                s[u]+=Math.max(t[v],s[v]);
            }
        }
        List<Integer>sel=new ArrayList<>();
        boolean[]ban=new boolean[n];
        top=0;
        st[top++]=0;
        while(top>0) {
            int u=st[--top];
            boolean take=!ban[u]&&t[u]>s[u];
            if(take)sel.add(u);
            for(int v:g[u])if(pa[v]==u) {
                ban[v]=take;
                st[top++]=v;
            }
        }
        return Map.of("weight",Math.max(t[0],s[0]),"selected",sel);
    }
    // Direct character-by-character search, including overlaps.
    static Map<String, Object> naive(Map<String, Object>m) {
        String t=S(m,"text"),p=S(m,"pattern");
        List<Integer>r=new ArrayList<>();
        if(p.isEmpty())r=bounds(t.length());
        else for(int i=0;i+p.length()<=t.length();i++) {
            int j=0;
            while(j<p.length()&&t.charAt(i+j)==p.charAt(j))j++;
            if(j==p.length())r.add(i);
        }
        return Map.of("positions",r);
    }
    static int[] pi(String p) {
        int[]a=new int[p.length()];
        for(int i=1;i<p.length();i++) {
            int j=a[i-1];
            while(j>0&&p.charAt(i)!=p.charAt(j))j=a[j-1];
            if(p.charAt(i)==p.charAt(j))j++;
            a[i]=j;
        }
        return a;
    }
    // Prefix-function search with fallback after every full match.
    static Map<String, Object> kmp(Map<String, Object>m) {
        String t=S(m,"text"),p=S(m,"pattern");
        List<Integer>r=new ArrayList<>();
        if(p.isEmpty())r=bounds(t.length());
        else {
            int[]a=pi(p);
            for(int i=0,j=0;i<t.length();i++) {
                while(j>0&&t.charAt(i)!=p.charAt(j))j=a[j-1];
                if(t.charAt(i)==p.charAt(j))j++;
                if(j==p.length()) {
                    r.add(i-j+1);
                    j=a[j-1];
                }
            }
        }
        return Map.of("positions",r);
    }
    // Rolling hash with exact verification of collision candidates.
    static Map<String, Object> rk(Map<String, Object>m) {
        String t=S(m,"text"),p=S(m,"pattern");
        List<Integer>r=new ArrayList<>();
        int n=t.length(),z=p.length();
        if(z==0)return Map.of("positions",bounds(n));
        if(z>n)return Map.of("positions",r);
        long h=0,g=0,pw=1;
        for(int i=0;i<z;i++) {
            h=(31*h+p.charAt(i))%101;
            g=(31*g+t.charAt(i))%101;
            if(i<z-1)pw=pw*31%101;
        }
        for(int i=0;i+z<=n;i++) {
            if(h==g) {
                int j=0;
                while(j<z&&t.charAt(i+j)==p.charAt(j))j++;
                if(j==z)r.add(i);
            }
            if(i+z<n) {
                g=(g-t.charAt(i)*pw)%101;
                if(g<0)g+=101;
                g=(31*g+t.charAt(i+z))%101;
            }
        }
        return Map.of("positions",r);
    }
    // Prefix-doubling suffix array followed by Kasai LCP.
    static Map<String, Object> suffix(Map<String, Object>m) {
        String s=S(m,"text");
        int n=s.length();
        if(n==0)return Map.of("sa",List.of(),"lcp",List.of(),"repeated","");
        int[]sa=new int[n],r=new int[n],nr=new int[n];
        for(int i=0;i<n;i++) {
            sa[i]=i;
            r[i]=s.charAt(i);
        }
        for(int k=1;;k*=2) {
            final int kk=k;
            Integer[]b=new Integer[n];
            for(int i=0;i<n;i++)b[i]=sa[i];
            Arrays.sort(b,(x,y)->r[x]!=r[y]?Integer.compare(r[x],r[y]):Integer.compare(x+kk<n?r[x+kk]:-1,y+kk<n?r[y+kk]:-1));
            for(int i=0;i<n;i++)sa[i]=b[i];
            nr[sa[0]]=0;
            for(int i=1;i<n;i++) {
                int x=sa[i-1],y=sa[i];
                nr[y]=nr[x]+(r[x]!=r[y]||(x+k<n?r[x+k]:-1)!=(y+k<n?r[y+k]:-1)?1:0);
            }
            System.arraycopy(nr,0,r,0,n);
            if(r[sa[n-1]]==n-1)break;
        }
        int[]pos=new int[n],lc=new int[n];
        for(int i=0;i<n;i++)pos[sa[i]]=i;
        for(int i=0,h=0;i<n;i++) {
            int z=pos[i];
            if(z==0)continue;
            int j=sa[z-1];
            while(i+h<n&&j+h<n&&s.charAt(i+h)==s.charAt(j+h))h++;
            lc[z]=h;
            if(h>0)h--;
        }
        int best=0;
        for(int x:lc)best=Math.max(best,x);
        String rep="";
        for(int i=0;i<n;i++)if(lc[i]==best&&best>0) {
            rep=s.substring(sa[i],sa[i]+best);
            break;
        }
        List<Integer>aa=new ArrayList<>(),bb=new ArrayList<>();
        for(int x:sa)aa.add(x);
        for(int x:lc)bb.add(x);
        return Map.of("sa",aa,"lcp",bb,"repeated",rep);
    }
    @SuppressWarnings("unchecked")
    // Trie, failure links, and reverse-failure count aggregation.
    static Map<String, Object> aho(Map<String, Object>m) {
        List<Object>ps=A(m,"patterns"),ts=A(m,"texts");
        List<int[]>go=new ArrayList<>();
        List<Integer>f=new ArrayList<>();
        go.add(new int[95]);
        f.add(0);
        for(Object po:ps) {
            String w=(String)po;
            int u=0;
            for(char ch:w.toCharArray()) {
                int c=ch-32;
                if(go.get(u)[c]==0) {
                    go.get(u)[c]=go.size();
                    go.add(new int[95]);
                    f.add(0);
                }
                u=go.get(u)[c];
            }
        }
        int n=go.size(),h=0,tail=0;
        int[]q=new int[n];
        for(int c=0;c<95;c++) {
            int v=go.get(0)[c];
            if(v!=0)q[tail++]=v;
        }
        while(h<tail) {
            int u=q[h++];
            for(int c=0;c<95;c++) {
                int v=go.get(u)[c];
                if(v!=0) {
                    f.set(v,go.get(f.get(u))[c]);
                    q[tail++]=v;
                }
                else go.get(u)[c]=go.get(f.get(u))[c];
            }
        }
        List<List<Long>>out=new ArrayList<>();
        for(Object to:ts) {
            long[]vis=new long[n];
            int u=0;
            for(char ch:((String)to).toCharArray()) {
                u=go.get(u)[ch-32];
                vis[u]++;
            }
            for(int x=n-1;x>0;x--)vis[f.get(q[x])]+=vis[q[x]];
            List<Long>row=new ArrayList<>();
            for(Object po:ps) {
                u=0;
                for(char ch:((String)po).toCharArray())u=go.get(u)[ch-32];
                row.add(vis[u]);
            }
            out.add(row);
        }
        return Map.of("counts",out);
    }
}
