package org.example.figures;



import static org.lwjgl.opengl.GL11.*;

public class Cube {

    public float x, y, z, angle;

    public Cube(float x, float y, float z, float angle) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.angle = angle;
    }

    public void draw(){
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glTranslatef(x, y, z);
        glRotated(angle, 0, 1f, 0f);
        glScalef(0.33f, 0.33f, 0.33f);

        glBegin(GL_QUADS);

        //Front
        glColor3f(230/255f, 126/255f, 34/255f);
        glVertex3f(-0.5f, -0.5f, 0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);

        //Back
        glColor3f(230/255f, 126/255f, 34/255f);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);
        glVertex3f(-0.5f, 0.5f, -0.5f);

        //Top face
        glColor3f(185/255f, 119/255f, 14/255f);
        glVertex3f(-0.5f, 0.5f, -0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);

        //Bottom face
        glColor3f(185/255f, 119/255f, 14/255f);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);
        glVertex3f(-0.5f, -0.5f, 0.5f);

        //Right face
        glColor3f(156/255f, 100/255f, 12/255f);
        glVertex3f(0.5f, -0.5f, -0.5f);
        glVertex3f(0.5f, 0.5f, -0.5f);
        glVertex3f(0.5f, 0.5f, 0.5f);
        glVertex3f(0.5f, -0.5f, 0.5f);

        //Left face
        glColor3f(156/255f, 100/255f, 12/255f);
        glVertex3f(-0.5f, -0.5f, -0.5f);
        glVertex3f(-0.5f, -0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, 0.5f);
        glVertex3f(-0.5f, 0.5f, -0.5f);
        glEnd();


    }

}
