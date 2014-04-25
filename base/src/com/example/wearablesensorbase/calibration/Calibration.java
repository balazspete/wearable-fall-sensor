package com.example.wearablesensorbase.calibration;

import com.example.wearablesensorbase.data.SensorMeasurement;

public class Calibration {
	
	public SensorMeasurement initial = new SensorMeasurement(0, 0, 0, 0, 0, 0, 0, 0);
	//need data type to store which movements relates to which axis
	String front, back, left, right, down, up;
	
	public void initialise(SensorMeasurement first) {
		System.out.println("Calibration: Initializing");
		initial = first;
	}
	
	public void callibrateLeftwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: left");
		calibrate(first,second,"LEFT");
		
	}
	
	public void callibrateRightwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: right");
		calibrate(first,second,"RIGHT");
	}
	
	public void callibrateForwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: forward");
		calibrate(first,second,"FRONT");
	}
	
	public void callibrateBackwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: backward");
		calibrate(first,second,"BACK");
	}
	
	public void callibrateDownwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: downward");
		calibrate(first,second,"DOWN");
	}
	
	public void callibrateUpwards(SensorMeasurement first, SensorMeasurement second) {
		System.out.println("Calibration: upward");
		calibrate(first,second,"UP");
	}
	
	public void calibrate(SensorMeasurement first, SensorMeasurement second, String direction){
		double changeXa, changeYa, changeZa;
		changeXa = first.acceleration.x.value - second.acceleration.x.value;
		changeYa = first.acceleration.y.value - second.acceleration.y.value;
		changeZa = first.acceleration.z.value - second.acceleration.z.value;
		
		//Calibrate X Axis
		if (Math.abs(changeXa) > Math.abs(changeYa) && Math.abs(changeXa) > Math.abs(changeZa)) {
            if (direction.equals("FRONT")){
            	if (changeXa > 0) {
            		front = "X";
            	}
            	else {
            		front = "-X";
            	}
            }
            else if (direction.equals("BACK")) {
            	if (changeXa > 0) {
            		back = "X";
            	}
            	else {
            		back = "-X";
            	}
            }
            else if (direction.equals("LEFT")) {
            	if (changeXa > 0) {
            		left = "X";
            	}
            	else {
            		left = "-X";
            	}
            }
            else if (direction.equals("RIGHT")) {
            	if (changeXa > 0) {
            		right = "X";
            	}
            	else {
            		right = "-X";
            	}
            }
            else if (direction.equals("DOWN")) {
            	if (changeXa > 0) {
            		down = "X";
            	}
            	else {
            		down = "-X";
            	}            }
            else if (direction.equals("UP")) {
            	if (changeXa > 0) {
            		up = "X";
            	}
            	else {
            		up = "-X";
            	}            }
        }
		//Calibrate Y Axis
		if (Math.abs(changeYa) > Math.abs(changeXa) && Math.abs(changeYa) > Math.abs(changeZa)) {
            if (direction.equals("FRONT")){
            	if (changeYa > 0) {
            		front = "Y";
            	}
            	else {
            		front = "-Y";
            	}
            }
            else if (direction.equals("BACK")) {
            	if (changeYa > 0) {
            		back = "Y";
            	}
            	else {
            		back = "-Y";
            	}
            }
            else if (direction.equals("LEFT")) {
            	if (changeYa > 0) {
            		left = "Y";
            	}
            	else {
            		left = "-Y";
            	}
            }
            else if (direction.equals("RIGHT")) {
            	if (changeYa > 0) {
            		right = "Y";
            	}
            	else {
            		right = "-Y";
            	}
            }
            else if (direction.equals("DOWN")) {
            	if (changeYa > 0) {
            		down = "Y";
            	}
            	else {
            		down = "-Y";
            	}            }
            else if (direction.equals("UP")) {
            	if (changeYa > 0) {
            		up = "Y";
            	}
            	else {
            		up = "-Y";
            	}            
            }
        }
		//Calibrate Z axis
		if (Math.abs(changeZa) > Math.abs(changeYa) && Math.abs(changeZa) > Math.abs(changeXa)) {
            if (direction.equals("FRONT")){
            	if (changeZa > 0) {
            		front = "Z";
            	}
            	else {
            		front = "-Z";
            	}
            }
            else if (direction.equals("BACK")) {
            	if (changeZa > 0) {
            		back = "Z";
            	}
            	else {
            		back = "-Z";
            	}
            }
            else if (direction.equals("LEFT")) {
            	if (changeZa > 0) {
            		left = "Z";
            	}
            	else {
            		left = "-Z";
            	}
            }
            else if (direction.equals("RIGHT")) {
            	if (changeZa > 0) {
            		right = "Z";
            	}
            	else {
            		right = "-Z";
            	}
            }
            else if (direction.equals("DOWN")) {
            	if (changeZa > 0) {
            		down = "Z";
            	}
            	else {
            		down = "-Z";
            	}            }
            else if (direction.equals("UP")) {
            	if (changeZa > 0) {
            		up = "Z";
            	}
            	else {
            		up = "-Z";
            	}            }
        }
	}
}
