package flounder.physics;

public interface Collidable {
	AABB getAABB();

	IntersectData intersects(AABB coll);

	ColliderRadial getRadialCollider();

	IntersectData intersects(ColliderRadial coll);

	IntersectData intersects(Collidable coll);
}