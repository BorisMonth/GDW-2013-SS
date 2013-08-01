package de.fhtrier.gdw.ss2013.settings;

public class DebugModeStatus {
    static protected boolean testModeStatus = false;

    static protected boolean physicTestStatus = false;
    
    static protected boolean tpCameraStatus = false;
    
    static String levelName = "testmap";

    static public boolean isTest() {
        return testModeStatus;
    }

    static public void setStatus(boolean status) {
        testModeStatus = status;
    }

    static public void setPhysicTest(boolean status) {
        physicTestStatus = status;
    }
    
    static public void setTPCamera(boolean status) {
        tpCameraStatus = status;
    }

    static public boolean isPhysicTest() {
        return physicTestStatus;
    }

    
    static public boolean isTPCamera() {
        return tpCameraStatus;
    }

	public static void setLevel(String levelname) {
		levelName = levelname;
	}

	public static String getLevelName() {
		return levelName;
	}
}
