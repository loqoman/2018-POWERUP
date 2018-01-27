package com.spartronics4915.frc2018.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.spartronics4915.frc2018.Constants;
import com.spartronics4915.frc2018.loops.Loop;
import com.spartronics4915.frc2018.loops.Looper;
import com.spartronics4915.lib.util.drivers.TalonSRX4915;
import com.spartronics4915.lib.util.drivers.TalonSRX4915Factory;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.NetworkTableInstance;


/**
 * A stub of a subsystem for learning purposes.
 */
public class Testbed extends Subsystem
{

    // the keyword, 'static', should only be used if you know what you're doing and, even then, sparingly.
    private static Testbed sInstance = null;

    // Sent from the PI
    public boolean fromPI = false;
    
    public boolean fromRIO = false;
    
    NetworkTableInstance foo;
    NetworkTable pi;
    NetworkTable values;
                    
    public static Testbed getInstance()
    {
        // Testbed is a singleton, meaning that only one object of the class
        // is ever instantiated.  Note that our constructor is private.
        if (sInstance == null)
        {
            sInstance = new Testbed();
        }
        return sInstance;
    }

    public enum SystemState
    {
        FORWARD_INTAKING,
        REVERSE_INTAKING,
        IDLING,
    }

    public enum WantedState
    {
        FORWARD_INTAKE,
        REVERSE_INTAKE,
        IDLE,
    }

    private SystemState mSystemState = SystemState.IDLING;
    private WantedState mWantedState = WantedState.IDLE;

    // actuators and sensors
    private TalonSRX4915 mMotor1 = null;
    private TalonSRX4915 mMotor2 = null;
    private DigitalInput mLimitSwitch = null; // invoke .get() to read
    private AnalogInput mPotentiometer = null;
    private Relay mLightSwitch = null;
    private Servo mServo = null; // Servo subclasses PWM

    private Testbed()
    {
        // Instantiate member variables (motors, etc...) here.
        boolean success = true;
        mMotor1 = TalonSRX4915Factory.createDefaultMotor(Constants.kTestbedMotor1Id);
        mMotor1.setControlMode(ControlMode.PercentOutput);
        if(!mMotor1.isValid())
        {
            logWarning("can't find motor 1, id:" + Constants.kTestbedMotor1Id);
            success = false;
        }

        mMotor2 = TalonSRX4915Factory.createDefaultMotor(Constants.kTestbedMotor2Id);
        mMotor2.setControlMode(ControlMode.PercentOutput);
        mMotor2.setInverted(true);
        if(!mMotor2.isValid())
        {
            logWarning("can't find motor 2, id:" + Constants.kTestbedMotor2Id);
            success = false;
        }

        mLimitSwitch = new DigitalInput(Constants.kTestbedLimitSwitchId);
        mPotentiometer = new AnalogInput(Constants.kTestbedPotentiometerId);
        mLightSwitch = new Relay(Constants.kTestbedLightSwitchId);
        mServo = new Servo(Constants.kTestbedServoId);

        logInitialized(success);
    }

    // Any public method that isn't @Overriding an abstract method on the Subsystem superclass
    // MUST BE SYNCHRONIZED (because it's called from an Action in another thread).

    private Loop mLoop = new Loop()
    {

        @Override
        public void onStart(double timestamp)
        {
            synchronized (Testbed.this)
            {
                mSystemState = SystemState.IDLING;
                logNotice("Testbed loop has started!");

            }
        }

        @Override
        public void onLoop(double timestamp)
        {
            synchronized (Testbed.this)
            {
                SystemState newState;
                switch (mSystemState)
                {
                    case FORWARD_INTAKING:
                        newState = handleForwardIntake();
                        break;
                    case REVERSE_INTAKING:
                        newState = handleReverseIntake();
                        break;
                    case IDLING:
                        newState = handleIdle();
                        break;
                    default:
                        newState = SystemState.IDLING;
                        break;
                }
                if (newState != mSystemState)
                {
                    logInfo("Testbed state from " + mSystemState + " to " + newState);
                    mSystemState = newState;
                }
            }
        }

        @Override
        public void onStop(double timestamp)
        {
            stop();
        }

    }; // end of assignment to mLoop

    public void setWantedState(WantedState wantedState)
    {
        mWantedState = wantedState;
    }

    @Override
    public void outputToSmartDashboard()
    {
        /* put useful state to the smart dashboard
         * 
         */
        SmartDashboard.putString("Testbed_Relay", mLightSwitch.get().getPrettyValue());
        SmartDashboard.putBoolean("Testbed_LimitSwitch", mLimitSwitch.get());
        SmartDashboard.putNumber("Testbed_Potentiometer", mPotentiometer.getValue());
        SmartDashboard.putNumber("Testbed_Servo", mServo.get());
    }

    // stop should also be synchronized, because it's also called from another thread
    @Override
    public synchronized void stop()
    {
        /*
         * This is here for motor safety, so that when we go into disabled mode,
         * the robot actually stops. You should stop your motors here.
         */
        runOpenLoop(0.0);
    }

    @Override
    public void zeroSensors()
    {
    }

    @Override
    public void registerEnabledLoops(Looper enabledLooper)
    {
        enabledLooper.register(mLoop);
    }

    private void runOpenLoop(double percentOutput)
    {
        // This is where code is run, this is where the networktables would go
        //goalposition.getSubTable("Pi");
        //Basic idea is to snag a table from the PI, and then get a value.
        //Get the table named "pi"
        //pi = foo.getTable("pi");
                
        //Get the sub-tables named "Values"
        //values = pi.getSubTable("Values");
        
        //NetworkTableEntry foo = values.getEntry("key");
        //FromPI = the value of the entry named key
        //fromPI = foo.getBoolean(false);
        
        String message = String.valueOf("True");
                
        logNotice("fromPI is set to ");
        logNotice(message);
        
     
        
        
        mMotor1.set(percentOutput);
        mMotor2.set(percentOutput);
        
        
    }

    /** describes the steps needed to progress from the current
     *  state to the wanted state.  Here, in open loop mode, we
     *  presume that the transition is instantaneous.
     * @return the current state
     */
    private SystemState defaultStateTransfer()
    {
        switch (mWantedState)
        {
            case FORWARD_INTAKE:
                return SystemState.FORWARD_INTAKING;
            case REVERSE_INTAKE:
                return SystemState.REVERSE_INTAKING;
            case IDLE:
                return SystemState.IDLING;
            default:
                return SystemState.IDLING;
        }
    }

    private SystemState handleForwardIntake()
    {
        runOpenLoop(1.0);
        if (mWantedState == WantedState.REVERSE_INTAKE)
        {
            return SystemState.IDLING;
        }
        return defaultStateTransfer();
    }

    private SystemState handleReverseIntake()
    {
        runOpenLoop(-1.0);
        if (mWantedState == WantedState.REVERSE_INTAKE)
        {
            return SystemState.IDLING;
        }
        return defaultStateTransfer();
    }

    private SystemState handleIdle()
    {
        return defaultStateTransfer();
    }

}
