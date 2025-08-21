package dev.drtheo.brp;

public class BoxCollisionResolver {

    public static class Vec3i {
        public int x, y, z;
        
        public Vec3i(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        
        public Vec3i add(Vec3i other) {
            return new Vec3i(x + other.x, y + other.y, z + other.z);
        }
        
        public Vec3i subtract(Vec3i other) {
            return new Vec3i(x - other.x, y - other.y, z - other.z);
        }
    }

    public static class Box {
        public Vec3i position;
        public Vec3i size;
        
        public Box(Vec3i position, Vec3i size) {
            this.position = position;
            this.size = size;
        }
        
        public boolean overlaps(Box other) {
            return (position.x < other.position.x + other.size.x &&
                    position.x + size.x > other.position.x &&
                    position.y < other.position.y + other.size.y &&
                    position.y + size.y > other.position.y &&
                    position.z < other.position.z + other.size.z &&
                    position.z + size.z > other.position.z);
        }
    }

    public static void resolveCollision(Box box1, Box box2) {
        if (!box1.overlaps(box2)) {
            return; // No collision to resolve
        }
        
        // Calculate overlap in each dimension
        int xOverlap = calculateOverlap(
            box1.position.x, box1.size.x, 
            box2.position.x, box2.size.x);
        
        int yOverlap = calculateOverlap(
            box1.position.y, box1.size.y, 
            box2.position.y, box2.size.y);
        
        int zOverlap = calculateOverlap(
            box1.position.z, box1.size.z, 
            box2.position.z, box2.size.z);
        
        // Find the minimal overlap direction
        int minOverlap = Math.min(Math.min(
            Math.abs(xOverlap), 
            Math.abs(yOverlap)), 
            Math.abs(zOverlap));
        
        // Resolve collision in the direction with minimal overlap
        if (minOverlap == Math.abs(xOverlap)) {
            // Move both boxes half the overlap distance in opposite directions
            int adjustment = xOverlap / 2;
            box1.position.x -= adjustment;
            box2.position.x += adjustment;
            
            // Handle remaining 1 pixel if overlap was odd
            if (xOverlap % 2 != 0) {
                if (adjustment > 0) {
                    box1.position.x--;
                } else {
                    box1.position.x++;
                }
            }
        } 
        else if (minOverlap == Math.abs(yOverlap)) {
            int adjustment = yOverlap / 2;
            box1.position.y -= adjustment;
            box2.position.y += adjustment;
            
            if (yOverlap % 2 != 0) {
                if (adjustment > 0) {
                    box1.position.y--;
                } else {
                    box1.position.y++;
                }
            }
        } 
        else {
            int adjustment = zOverlap / 2;
            box1.position.z -= adjustment;
            box2.position.z += adjustment;
            
            if (zOverlap % 2 != 0) {
                if (adjustment > 0) {
                    box1.position.z--;
                } else {
                    box1.position.z++;
                }
            }
        }
    }
    
    private static int calculateOverlap(int pos1, int size1, int pos2, int size2) {
        int end1 = pos1 + size1;
        int end2 = pos2 + size2;
        
        if (pos1 < pos2) {
            return end1 - pos2;
        } else {
            return pos1 - end2;
        }
    }

    // Example usage
    public static void main(String[] args) {
        Box box1 = new Box(new Vec3i(0, 0, 0), new Vec3i(10, 10, 10));
        Box box2 = new Box(new Vec3i(8, 5, 5), new Vec3i(10, 10, 10));
        
        System.out.println("Before resolution - Overlapping: " + box1.overlaps(box2));
        System.out.println("Box1 position: (" + box1.position.x + ", " + box1.position.y + ", " + box1.position.z + ")");
        System.out.println("Box2 position: (" + box2.position.x + ", " + box2.position.y + ", " + box2.position.z + ")");
        
        resolveCollision(box1, box2);
        
        System.out.println("\nAfter resolution - Overlapping: " + box1.overlaps(box2));
        System.out.println("Box1 position: (" + box1.position.x + ", " + box1.position.y + ", " + box1.position.z + ")");
        System.out.println("Box2 position: (" + box2.position.x + ", " + box2.position.y + ", " + box2.position.z + ")");
    }
}