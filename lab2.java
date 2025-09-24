class Maain
{
    int x,y;
    Maain(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
}

class lab2 {
    public static void main(String[] args)
    {
        Maain a=new Maain(10,20);
        System.out.println("x="+a.x);
        System.out.println("y="+a.y);
    }
}
