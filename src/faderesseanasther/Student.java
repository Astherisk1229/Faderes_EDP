/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package faderesseanasther;

/**
 *
 * @author student
 */
public class Student {
    private int age;
    private String name;
    
    
    public void setAge(int age){
        this.age = age;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void displayDetails(){
        System.out.println("The name is: " + name + " and the age is: " + age);
    }
}
