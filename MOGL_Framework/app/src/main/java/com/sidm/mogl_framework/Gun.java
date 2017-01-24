package com.sidm.mogl_framework;

/**
 * Created by Daniel on 23/1/2017.
 */

public class Gun {
    public String name;
    public int i_damage;
    public float f_fireRate;
    // private float f_fireDebounceTimer;

    public int i_ammoInMag;
    public int i_totalAmmo;
    public int i_maxAmmo;
    public int i_magCap;

    Gun()
    {

    }

    Gun(String name,float fireRate,int damage,int magCap,int maxAmmo)
    {
        this.name = name;
        this.i_damage = damage;
        this.f_fireRate = fireRate;

        this.i_magCap = magCap;
        this.i_ammoInMag = magCap;
        this.i_maxAmmo = maxAmmo;
        this.i_totalAmmo = i_maxAmmo;
    }

    void Reload()
    {
        if(this.i_totalAmmo > this.i_magCap)
        {
            this.i_ammoInMag = this.i_magCap;
        }
        else
        {
            this.i_ammoInMag = this.i_totalAmmo;
        }
    }

}
