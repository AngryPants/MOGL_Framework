package com.sidm.mogl_framework;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.Objects;
import java.util.Vector;

/**
 * Created by Daniel on 5/12/2016.
 */

public class Bullet {
    public Transform transform;
    private final float f_travelSpeed = 10.f;
    Vector<Zombie> zombieList;
    boolean b_isActive;
    public int damage;
    public Vector2 view;
    MeshBuilder.Mesh bulletMesh;
    Textures texture;
    static float screenSize;

    //private Object mutexObject;

    public Bullet(Vector2 pos, Vector2 view,Vector<Zombie> zombieList)
    {
        //this.notSprite = new SpriteAnimation();
        this.transform = new Transform();
        this.transform.SetPosition(pos);
        this.view = view;
        this.b_isActive = true;
        this.zombieList = zombieList;
        this.transform.SetScale(0.5f,0.2f);


        //mutexObject = new Object();
        bulletMesh = MeshBuilder.GetMesh("Quad");
        texture = new Textures();
        texture.handles[0] = TextureManager.GetTextureID("bullet");
        //notSprite.SetBitmap(mesh,(int)this.transform.GetScale().x,(int)this.transform.GetScale().y);
    }

    public void Set(Vector2 pos, Vector2 view)
    {

        this.transform.SetPosition(pos);
        //this.transform.SetScale(screenSize/10,screenSize/10);
        this.view = view;
        this.transform.SetScale(0.5f,0.2f);
    }
    public void Update(double dt)
    {
        if(this.b_isActive==false)
        {
            return;
        }

        this.transform.Translate(view.Times((float)dt).Times(f_travelSpeed));
        for(Zombie zombie:zombieList)
        {
            if(zombie.b_isActive == true)
            {
                if(CollisionSystem.CollisionCircleCircle(this.transform.GetPosition(),this.transform.GetScale().x/2,zombie.transform.GetPosition(),zombie.transform.GetScale().x/2) == true)
                {
                    b_isActive = false;
                    zombie.health -= damage;
                    return;
                }
            }
        }
        this.transform.SetRotation((float)Math.toDegrees(Math.atan2(view.y,view.x)));
    }


}
