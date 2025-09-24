/*//Calculator
class Calculator
{
    int add(int a,int b)
    {
         return a+b;
    }
    int sub(int a,int b)
    {
        return a-b;
    }
    int mul(int a,int b)
    {
         return a*b;
    }
    float div(int a,int b)
    {
        return (float)a/b;
    }
}

class lab
{
    public static void main(String[] a)
    {
        int x=30,y=4;
        Calculator o=new Calculator();
        System.out.println("Sum = "+o.add(x, y));
        System.out.println("Sum = "+o.sub(x, y));
        System.out.println("Sum = "+o.mul(x, y));
        System.out.println("Sum = "+o.div(x, y));
    }
}




class Rectangle{
    int length;
    int breadth;
    int calcarea()
    {
        int area=length*breadth;
        return area;
    }
    int calcpe()
    {
        int peri=2*(length+breadth);
        return peri;
    }
}

class lab {
    public static void main(String[] a)
    {
        Rectangle o1=new Rectangle();
        o1.length=20;
        o1.breadth=10;
        Rectangle o2=new Rectangle();
        o2.length=30;
        o2.breadth=50;
        Rectangle o3=new Rectangle();
        o3.length=40;
        o3.breadth=90;
        System.out.println("     Area 1 = "+o1.calcarea());
        System.out.println("Perimeter 1 = "+o1.calcpe());
        System.out.println("     Area 2 = "+o2.calcarea());
        System.out.println("Perimeter 2 = "+o2.calcpe());
        System.out.println("     Area 3 = "+o3.calcarea());
        System.out.println("Perimeter 3 = "+o3.calcpe());
    }
}

*/

//Bank acc
class BankAccount
{
    short acc_no;
    String acc_holder;
    int balance;
    void deposit()
    {
        balance*=2;
    }
    void withdrawal()
    {
        balance/=2;
    }
}

class lab
{
    public static void main(String[] a)
    {
        BankAccount x=new BankAccount();
        x.acc_holder="Anagha";
        x.acc_no=(short)123456;
        x.balance=30000;
        System.out.println("Remaining Balance : "+x.balance);
        x.deposit();
        System.out.println("Remaining Balance after deposit : "+x.balance);
        x.withdrawal();
        System.out.println("Remaining Balance after withdrawal : "+x.balance);
        
    }
}


/* 
//person
class person{
    String name;
    byte age;
    void display()
    {
        System.out.println("Name : "+name+"\nAge : "+age);
    }
}

class lab{
    public static void main(String[] a)
    {
        person p=new person();
        p.name="Anagha";
        p.age=18;
        p.display();
    }
}*/