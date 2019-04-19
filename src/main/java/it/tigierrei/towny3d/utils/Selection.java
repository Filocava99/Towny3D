package it.tigierrei.towny3d.utils;

public class Selection {

    private Vector vector1;
    private Vector vector2;

    public Vector getVector1() {
        return vector1;
    }

    public Vector getVector2() {
        return vector2;
    }

    public void setVector1(Vector vector1) {
        if(vector2 == null) {
            this.vector1 = vector1;
        }else{
            setLowerAndHigher(vector1,vector2);
        }
    }

    public void setVector2(Vector vector2) {
        if(vector1 == null) {
            this.vector2 = vector2;
        }else{
            setLowerAndHigher(vector1,vector2);
        }
    }

    private int getLower(int x, int y){
        return (x <= y) ? x : y;
    }

    private int getHigher(int x, int y){
        return (x > y) ? x : y;
    }

    private void setLowerAndHigher(Vector vector1,Vector vector2){
        int lowerX = getLower(vector1.getX(),vector2.getX());
        int lowerY = getLower(vector1.getY(),vector2.getY());
        int lowerZ = getLower(vector1.getZ(),vector2.getZ());

        int higherX = getHigher(vector1.getX(),vector2.getX());
        int higherY = getHigher(vector1.getY(),vector2.getY());
        int higherZ = getHigher(vector1.getZ(),vector2.getZ());


        this.vector1 = new Vector(lowerX,lowerY,lowerZ);
        this.vector2 = new Vector(higherX,higherY,higherZ);
    }
}