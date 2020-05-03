package com.example.rha;

public class Memberlist
{
  public  String Name,Username,Profile,Address,Phoneno;

  public Memberlist()
  {

  }
    public Memberlist(String name, String username, String profile, String address, String phoneno) {
        Name = name;
        Username = username;
        Profile = profile;
        Address = address;
        Phoneno = phoneno;
    }

    public String getName1() {
        return Name;
    }

    public void setName1(String name) {
        Name = name;
    }

    public String getUsername1() {
        return Username;
    }

    public void setUsername1(String username) {
        Username = username;
    }

    public String getProfile1() {
        return Profile;
    }

    public void setProfile1(String profile) {
        Profile = profile;
    }

    public String getAddress1() {
        return Address;
    }

    public void setAddress1(String address) {
        Address = address;
    }

    public String getPhoneno1() {
        return Phoneno;
    }

    public void setPhoneno1(String phoneno) {
        Phoneno = phoneno;
    }
}
