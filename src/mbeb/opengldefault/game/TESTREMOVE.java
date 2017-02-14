/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mbeb.opengldefault.game;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 *
 * @author Erik
 */
public class TESTREMOVE {
	public static void main(String[] args) {
		System.out.println(new Matrix4f().rotate(new Quaternionf(new AxisAngle4f(12.34f, 3, 4, 5)).normalize()).getScale(new Vector3f()));
	}
}
