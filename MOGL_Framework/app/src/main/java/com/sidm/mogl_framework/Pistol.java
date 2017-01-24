package com.sidm.mogl_framework;

/**
 * Created by Daniel on 24/1/2017.
 */

public class Pistol extends Gun {
    Pistol()
    {
        this.name = "Pistol";
        this.i_ammoInMag = 6;
        this.i_totalAmmo = 999;
        this.i_magCap = 6;
        this.i_maxAmmo = 999;
        this.i_damage = 10;
        this.f_fireRate = 2;
    }

    @Override
    void Reload() {
        this.i_ammoInMag = this.i_magCap;
    }
}
