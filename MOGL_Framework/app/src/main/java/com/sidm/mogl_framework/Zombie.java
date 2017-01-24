package com.sidm.mogl_framework;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Daniel on 5/12/2016.
 */

public class Zombie extends Character
{


    public int i_damage;
    private Player target;
    private float f_attackTimer = 0.f;
    private float f_attackSpeed = 1.f;
    static int i_aliveCount = 0;
    boolean b_isActive;
    float screenSize;

    public MeshBuilder.Mesh zombieMesh;
    public Textures texture;

    public Zombie(Player target,Vector2 pos, Vector2 scale , Bitmap mesh)
    {
        this.transform.SetScale(scale);
        this.transform.SetPosition(pos);
        this.target = target;
        this.maxHealth = 10;
        this.health = this.maxHealth;
        this.i_damage = 10;
        this.movementSpeed = 1.f;

        /*sprite = new SpriteAnimation();
        sprite.SetBitmap(mesh,(int)this.transform.GetScale().x,(int)this.transform.GetScale().y);
        Animation2D anim = new Animation2D();
        anim.Set(8,9,0,8,1.f,true);
        sprite.SetAnimation2D(anim);*/

        //set mesh

        zombieMesh = MeshBuilder.GetMesh("Quad");
        texture = new Textures();
        texture.handles[0] = TextureManager.GetTextureID("zombie");


    }

    public void Reinit(Player target,Vector2 pos)
    {
        this.transform.SetPosition(pos);
        this.target = target;
        this.health = 10;
        this.i_damage = 10;
        this.movementSpeed = 1.f;

    }

    private Vector2 GetDirectionToTarget()
    {
        return  (this.target.transform.GetPosition().Minus(this.transform.GetPosition()));
    }

    private void Attack(double dt)
    {
        f_attackTimer+=dt;
        if(f_attackTimer >= 1.f/f_attackSpeed)
        {
            f_attackTimer =0.f;
            target.ReceiveDamage(i_damage);
        }

    }

    public void Update(double dt)
    {
        if(this.health <= 0)
        {
            this.i_aliveCount--;
            Player.i_score++;
            this.b_isActive = false;
            return;
        }
        if(target == null)
        {
            return;
        }


        if(GetDirectionToTarget().LengthSquared() > (screenSize / 100.f + (target.transform.GetScale().x * 0.5f)) * (screenSize / 100.f + (target.transform.GetScale().x * 0.5f)))
        {
            Vector2 dir = GetDirectionToTarget();
            this.transform.Translate(dir.Normalised().Times(movementSpeed).Times((float)dt));
           // System.out.println(this.transform.GetPosition().x);
            //hard code cuz the png is facing up
            this.transform.SetRotation((float)Math.toDegrees(Math.atan2(dir.y,dir.x)));
        }
        else
        {
            Attack(dt);
        }

    }



}
