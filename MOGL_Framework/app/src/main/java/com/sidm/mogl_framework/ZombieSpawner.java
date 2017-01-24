package com.sidm.mogl_framework;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Vector;

/**
 * Created by Daniel on 5/12/2016.
 */

public class ZombieSpawner {
    static Bitmap mesh = null;
    private Player target;
    //private float f_spawnFrequency = 1.f;
    private float f_spawnFrequency = 1.f;
    private float f_spawnTimer = 1.f;
    private int f_maxCount = 30;
    public Vector2 scale;
    public float screenSize;

    public GLESRenderer glesRenderer;

    public Vector<Zombie> zombieList;

    public void Init(Bitmap mesh)
    {
        this.mesh = mesh;
    }

    public ZombieSpawner(Player target, Vector2 scale)
    {
        this.target = target;
        this.scale = scale;
        zombieList = new Vector<Zombie>();

    }

    private void SpawnZombie()
    {
        if(Zombie.i_aliveCount >= f_maxCount)
        {
            return;
        }
        float spawnRadius = 5;
        float minSpawnX = this.target.transform.GetPosition().x - spawnRadius;
        float minSpawnY = this.target.transform.GetPosition().y - spawnRadius;
        float maxSpawnX = this.target.transform.GetPosition().x + spawnRadius;
        float maxSpawnY = this.target.transform.GetPosition().y + spawnRadius;
        float x = (float)(minSpawnX + Math.random() * (maxSpawnX- minSpawnX));
        float y = (float)(minSpawnY + Math.random() * (maxSpawnY- minSpawnY));

        Vector2 spawnLocation = new Vector2(x,y);
        for(Zombie zombie : zombieList)
        {

            if(zombie.b_isActive == false)
            {
                zombie.Reinit(target,spawnLocation);
                zombie.i_aliveCount++;
                zombie.screenSize = screenSize;
                zombie.b_isActive = true;
                return;
            }
        }
        Zombie temp = new Zombie(target,spawnLocation,scale,mesh);
        temp.i_aliveCount++;
        temp.screenSize = screenSize;
        temp.b_isActive = true;
        //System.out.println("Zombie Spawned");
        zombieList.add(temp);
    }

    public void Update(double dt)
    {
        f_spawnTimer += dt;
        if(f_spawnTimer >= 1.f/f_spawnFrequency)
        {
            SpawnZombie();
            f_spawnTimer = 0.f;
        }

        for(Zombie zombie : zombieList)
        {
            if(zombie.b_isActive == true)
            {
                zombie.Update(dt);
            }
        }

    }

    public void DrawAllZombie()
    {

        Matrix4x4Stack modelStack = glesRenderer.modelStack;
        for(Zombie zombie:zombieList)
        {
            if(zombie.b_isActive == true)
            {
                modelStack.PushMatrix();

                modelStack.Translate(zombie.transform.GetPosition().x,zombie.transform.GetPosition().y,1);
                modelStack.Rotate(zombie.transform.GetRotation(),0,0,1);
                modelStack.Scale(zombie.transform.GetScale().x,zombie.transform.GetScale().y,1);

                glesRenderer.Render(zombie.zombieMesh,zombie.texture);
                modelStack.PopMatrix();
            }
        }
    }
}
