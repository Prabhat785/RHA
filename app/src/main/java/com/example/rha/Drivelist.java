package com.example.rha;

public class Drivelist {
    public String Username, Noofmemeber, date, drivelocation, picuplocation, profilepic, sponsor, time, uid,Smiles;
   Boolean Status;

   public  Drivelist()
   {

   }
    public Drivelist(String noofmember, String username, String date, String drivelocation, String picuplocation, String profile, String sponsor, String uid,String smiles,Boolean status) {
        this.Noofmemeber = noofmember;
        this.Username = username;
        this.date = date;
        this.drivelocation = drivelocation;
        this.picuplocation = picuplocation;
        this.profilepic = profile;
        this.sponsor = sponsor;
        this.time = time;
        this.uid = uid;
        this.Status =status;
        this.Smiles=smiles;
    }

    public Boolean getStatus1() {
        return Status;
    }

    public void setStatus1(Boolean status) {
        Status = status;
    }

    public String getSmiles1() {
        return Smiles;
    }

    public void setSmiles1(String smiles) {
        Smiles = smiles;
    }

    public String getNoofmemeber1() {
        return Noofmemeber;
    }

    public void setNoofmemeber1(String noofmemeber) {
        Noofmemeber = noofmemeber;
    }

    public String getUsername1() {
        return Username;
    }

    public void setUsername1(String Username) {
        this.Username = Username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDrivelocation() {
        return drivelocation;
    }

    public void setDrivelocation(String drivelocation) {
        this.drivelocation = drivelocation;
    }

    public String getPicuplocation() {
        return picuplocation;
    }

    public void setPicuplocation(String picuplocation) {
        this.picuplocation = picuplocation;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
