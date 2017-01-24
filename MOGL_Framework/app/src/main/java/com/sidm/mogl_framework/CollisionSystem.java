package com.sidm.mogl_framework;

/**
 * Created by AFTERSHOCK on 28/11/2016.
 */

public class CollisionSystem {

	//Constructor(s)
	public CollisionSystem() {}

	//Helper Function(s)
	static boolean CollisionAABB(final Vector2 posA, final Vector2 sizeA, final Vector2 posB, final Vector2 sizeB) {
		float combinedWidth = sizeA.x + sizeB.x;
		float combinedHeight = sizeA.y + sizeB.y;
		if (Math.abs(posA.x - posB.x) <= combinedWidth * 0.5f && Math.abs(posA.y - posB.y) <= combinedHeight * 0.5f) {
			return true;
		}

		return false;
	}

	static boolean CollisionCircleCircle(final Vector2 positionA, final float radiusA, final Vector2 positionB, final float radiusB) {
		Vector2 a = positionA.Minus(positionB);
		//System.out.println("LengthSquared: "+ a.LengthSquared());
		//System.out.println("Radius Squared: "+ (radiusA + radiusB) * (radiusA + radiusB));
		if (a.LengthSquared() < (radiusA + radiusB) * (radiusA + radiusB)) {
			System.out.println("Hit!");
			return true;
		}

		System.out.println("No Hit!");
		return false;
	}

}
