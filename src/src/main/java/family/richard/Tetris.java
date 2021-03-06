package family.richard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zzhang on 2018-04-15.
 */
public class Tetris {
    private static final int N = 13;

    private static class E {
        public E(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public E rotate90(char axis) {
            if (axis == 'z')
                return new E(-y, x, z);
            if (axis == 'y')
                return new E(-z, y, x);
            if (axis == 'x')
                return new E(x, -z, y);
            return null;
        }

        public E rotate180(char axis) {
            if (axis == 'z')
                return new E(-x, -y, z);
            if (axis == 'y')
                return new E(-x, y, -z);
            if (axis == 'x')
                return new E(x, -y, -z);
            return null;
        }

        public E rotate270(char axis) {
            if (axis == 'z')
                return new E(y, -x, z);
            if (axis == 'y')
                return new E(z, y, -x);
            if (axis == 'x')
                return new E(x, z, -y);
            return null;
        }

        public E trans(int t) {
            if (t == 1)
                return rotate90('z');
            if (t == 2)
                return rotate180('z');
            if (t == 3)
                return rotate270('z');
            E x = rotate180('x');
            if (t == 4)
                return x;
            if (t == 5)
                return x.rotate90('z');
            if (t == 6)
                return x.rotate180('z');
            if (t == 7)
                return x.rotate270('z');
            if (t == 8)
                return this;
            if (t == 9)
                return rotate90('y');
            if (t == 10)
                return rotate180('y');
            if (t == 11)
                return rotate270('y');
            x = rotate180('z');
            if (t == 12)
                return x;
            if (t == 13)
                return x.rotate90('y');
            if (t == 14)
                return x.rotate180('y');
            if (t == 15)
                return x.rotate270('y');
            if (t == 16)
                return this;
            if (t == 17)
                return rotate90('x');
            if (t == 18)
                return rotate180('x');
            if (t == 19)
                return rotate270('x');
            x = rotate180('y');
            if (t == 20)
                return x;
            if (t == 21)
                return x.rotate90('x');
            if (t == 22)
                return x.rotate180('x');
            if (t == 23)
                return x.rotate270('x');
            return null;
        }

        public int x, y, z;
    }

    private static class S {
        public E[] e;
        public int minx, maxx;
        public int miny, maxy;
        public int minz, maxz;

        public S(E[] e) {
            this.e = e;
        }

        public S trans(int t) {
            S x = new S(null);
            x.e = new E[e.length];
            for (int i = 0; i < e.length; i++)
                x.e[i] = e[i].trans(t);
            return x;
        }

        public void regular() {
            minx = miny = minz = Integer.MAX_VALUE;
            maxx = maxy = maxz = Integer.MIN_VALUE;
            for (E x : e) {
                minx = Math.min(minx, x.x);
                maxx = Math.max(maxx, x.x);
                miny = Math.min(miny, x.y);
                maxy = Math.max(maxy, x.y);
                minz = Math.min(minz, x.z);
                maxz = Math.max(maxz, x.z);
            }
            while (minx > 0) {
                for (E x : e) {
                    x.x--;
                }
                minx--;
                maxx--;
            }
            while (minx < 0) {
                for (E x : e) {
                    x.x++;
                }
                minx++;
                maxx++;
            }
            while (miny > 0) {
                for (E x : e) {
                    x.y--;
                }
                miny--;
                maxy--;
            }
            while (miny < 0) {
                for (E x : e) {
                    x.y++;
                }
                miny++;
                maxy++;
            }
            while (minz > 0) {
                for (E x : e) {
                    x.z--;
                }
                minz--;
                maxz--;
            }
            while (minz < 0) {
                for (E x : e) {
                    x.z++;
                }
                minz++;
                maxz++;
            }
        }

        public boolean equals(S x) {
            if (minx != x.minx || maxx != x.maxx || miny != x.miny || maxy != x.maxy || minz != x.minz || maxz != x.maxz)
                return false;
            if (e.length != x.e.length)
                return false;
            for (int i = 0; i < e.length; i++) {
                if (e[i].x != x.e[i].x || e[i].y != x.e[i].y || e[i].z != x.e[i].z)
                    return false;
            }
            return true;
        }

        public boolean place(boolean[][][] box, int xoff, int yoff, int zoff) {
            for (E x : e) {
                if (box[x.x + xoff][x.y + yoff][x.z + zoff])
                    return false;
            }
            for (E x : e) {
                box[x.x + xoff][x.y + yoff][x.z + zoff] = true;
            }
            return true;
        }

        public void revert(boolean[][][] box, int xoff, int yoff, int zoff) {
            for (E x : e) {
                box[x.x + xoff][x.y + yoff][x.z + zoff] = false;
            }
        }
    }

    private static class B {
        public S[] s = new S[24];

        public void compact() {
            for (int i = 0; i < s.length; i++) {
                Arrays.sort(s[i].e, new Comparator<E>() {
                    @Override
                    public int compare(E o1, E o2) {
                        int c = Integer.compare(o1.x, o2.x);
                        if (c != 0)
                            return c;
                        c = Integer.compare(o1.y, o2.y);
                        if (c != 0)
                            return c;
                        c = Integer.compare(o1.z, o2.z);
                        return c;
                    }
                });
            }
            boolean[] d = new boolean[s.length];
            for (int i = 0; i < s.length; i++)
                for (int j = i + 1; j < s.length; j++) {
                    if (s[i].equals(s[j]))
                        d[j] = true;
                }
            List<S> l = new ArrayList<>();
            for (int i = 0; i < s.length; i++)
                if (!d[i])
                    l.add(s[i]);
            s = l.toArray(new S[l.size()]);
        }
    }

    private void init() {
        b = new B[N];
        for (int i = 0; i < N; i++)
            b[i] = new B();
        b[0].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(0, 1, 1), new E(0, 1, 0)});
        b[1].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(2, 0, 0), new E(1, 0, 1), new E(1, 1, 1)});
        b[2].s[0] = new S(new E[]{new E(0, 0, 0), new E(0, 0, 1), new E(0, 1, 0), new E(1, 1, 0), new E(2, 1, 0)});
        b[3].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(2, 0, 0), new E(2, 0, 1), new E(0, 1, 0)});
        b[4].s[0] = new S(new E[]{new E(1, 0, 0), new E(0, 1, 0), new E(1, 1, 0), new E(2, 1, 0), new E(1, 2, 0)});
        b[5].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(2, 0, 0), new E(0, 1, 0), new E(0, 1, 1)});
        b[6].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(2, 0, 0), new E(0, 1, 0), new E(2, 1, 0)});
        b[7].s[0] = new S(new E[]{new E(1, 0, 0), new E(2, 0, 0), new E(0, 1, 0), new E(0, 1, 1), new E(1, 1, 0)});
        b[8].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(2, 0, 0), new E(1, 0, 1), new E(0, 1, 0)});
        b[9].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(1, 0, 1), new E(2, 0, 1), new E(1, 1, 0)});
        b[10].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(1, 1, 0), new E(1, 1, 1), new E(2, 1, 1)});
        b[11].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(1, 0, 1), new E(1, 1, 1), new E(2, 1, 1)});
        b[12].s[0] = new S(new E[]{new E(0, 0, 0), new E(1, 0, 0), new E(1, 1, 0), new E(1, 2, 0), new E(2, 2, 0)});
        for (B x : b) {
            for (int t = 1; t < x.s.length; t++) {
                x.s[t] = x.s[0].trans(t);
            }
            for (S ss : x.s) {
                ss.regular();
            }
            x.compact();
        }
    }

    private boolean place(int bidx, int sidx, int xoff, int yoff, int zoff) {
        try {
            stack[head++] = bidx;
            stack[head++] = sidx;
            stack[head++] = xoff;
            stack[head++] = yoff;
            stack[head++] = zoff;
            if (true) {
                if (bidx == 3) {
                    for (int i = 0; i <= bidx; i++)
                        System.out.print(stack[5 * i] + ":" + stack[5 * i + 1] + " ");
                    System.out.println();
                }
            }
            S ss = b[bidx].s[sidx];
            for (E x : ss.e) {
                try {
                    if (box[x.x + xoff][x.y + yoff][x.z + zoff])
                        return false;
                } catch (Exception e) {
                    System.out.println();
                }
            }
            for (E x : ss.e) {
                box[x.x + xoff][x.y + yoff][x.z + zoff] = true;
            }
            if (bidx >= N - 1) {
                System.out.println("solution");
                int ph = 0;
                for (int i = 0; i < N; i++) {
                    int bi = stack[ph++];
                    int si = stack[ph++];
                    int xo = stack[ph++];
                    int yo = stack[ph++];
                    int zo = stack[ph++];
                    System.out.print(bi + ":" + si);
                    for (E x : b[bi].s[si].e) {
                        System.out.print("[" + x.x + "," + x.y + "," + x.z + "]");
                    }
                    System.out.println(" on " + xo + "," + yo + "," + zo);
                }
                return true;
            }
            if (place(bidx + 1))
                return true;
            for (E x : ss.e) {
                box[x.x + xoff][x.y + yoff][x.z + zoff] = false;
            }
            return false;
        } finally {
            head -= 5;
        }
    }

    private boolean place(int bidx, int sidx) {
        S ss = b[bidx].s[sidx];
        for (int xoff = 0; xoff < 4 - ss.maxx; xoff++)
            for (int yoff = 0; yoff < 4 - ss.maxy; yoff++)
                for (int zoff = 0; zoff < 4 - ss.maxz; zoff++)
                    if (place(bidx, sidx, xoff, yoff, zoff))
                        return true;
        return false;
    }

    private boolean place(int bidx) {
        for (int i = 0; i < b[bidx].s.length; i++)
            if (place(bidx, i))
                return true;
        return false;
    }

    public void run() {
        init();
//        for (int i = 0; i < b.length; i++)
//            System.out.println(i + " " + b[i].s.length);
        box = new boolean[4][4][4];
        stack = new int[5 * N];
        head = 0;
        place(0);
    }

    private B[] b;
    private boolean[][][] box;
    private int[] stack;
    private int head;

/*
0:0 1:0 2:0 3:6
solution
0:0[0,0,0][0,1,0][0,1,1][1,0,0] on 0,0,0
1:0[0,0,0][1,0,0][1,0,1][1,1,1][2,0,0] on 0,0,1
2:0[0,0,0][0,0,1][0,1,0][1,1,0][2,1,0] on 0,2,0
3:6[0,0,0][0,0,1][1,0,1][2,0,1][2,1,1] on 0,0,2
4:1[0,0,1][0,1,0][0,1,1][0,1,2][0,2,1] on 1,1,0
5:8[0,1,0][1,0,0][1,0,1][1,0,2][1,1,0] on 2,1,0
6:2[0,0,0][0,1,0][1,1,0][2,0,0][2,1,0] on 1,1,3
7:2[0,1,0][1,0,0][1,1,0][2,0,0][2,0,1] on 1,0,0
8:4[0,0,1][0,1,1][1,1,0][1,1,1][2,1,1] on 0,2,2
9:10[0,1,0][0,1,1][1,0,1][1,1,1][1,1,2] on 2,2,1
10:11[0,0,1][0,1,0][0,1,1][1,0,1][1,0,2] on 2,0,1
11:9[0,0,1][0,0,2][1,0,1][1,1,0][1,1,1] on 2,2,0
12:5[0,0,1][0,0,2][0,1,1][0,2,0][0,2,1] on 0,1,1
*/
    public static void main(String[] args) {
        new Tetris().run();
    }
}
