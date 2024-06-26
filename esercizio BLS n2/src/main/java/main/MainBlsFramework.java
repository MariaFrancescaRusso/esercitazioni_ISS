package main;

import it.unibo.bls.devices.DeviceConfig;
import it.unibo.bls.devices.arduino.ButtonProxyArduino;
import it.unibo.bls.devices.arduino.LedProxyArduino;
import it.unibo.bls.devices.gui.ButtonAsGui;
import it.unibo.bls.interfaces.*;
import it.unibo.bls.utils.Utils;
import it.unibo.blsFramework.kotlin.applLogic.AnotherApplLogic;
import it.unibo.blsFramework.kotlin.applLogic.BlsApplicationLogic;
import it.unibo.blsFramework.concreteDevices.LedObserver;
import it.unibo.bls.devices.gui.LedAsGui;
import it.unibo.blsFramework.interfaces.*;
import it.unibo.blsFramework.interfaces.IApplListener;
import it.unibo.blsFramework.listener.ButtonObserver;
import it.unibo.blsFramework.models.ButtonModel;
import it.unibo.blsFramework.models.LedModel;

public class MainBlsFramework implements IBlsFramework {
	private String cmdName;
	private ILedModel ledmodel;
	private IButtonModel buttonmodel;
	private IAppLogic applLogic;
	protected IApplListener buttonObserver;
	protected ILedObserver ledObserver;
	protected IObservable concreteButton = null;
	protected ILed concreteLed = null;

	//Factory method
	public static MainBlsFramework createTheSystem(String cmdName)
    {
		return new MainBlsFramework(cmdName);
	}

	protected MainBlsFramework(String cmdName) {
		Utils.showSystemInfo();
		this.cmdName = cmdName;
		createLogicalComponents();
		configureSystemArchitecture();
	}

	protected void createLogicalComponents() {
		ledmodel       = LedModel.createLed();
        buttonmodel    = ButtonModel.createButton(cmdName);
		applLogic      = new BlsApplicationLogic();
		buttonObserver = ButtonObserver.createButtonListener();
		ledObserver    = LedObserver.create();
	}

	protected void configureSystemArchitecture() {
		buttonmodel.addObserver(buttonObserver);
        ledmodel.addObserver(ledObserver);
        setApplLogic( applLogic );
 	}
	/*
	 * Setter methods
	 */
	@Override //IBlsFrameworkFsm
	public void setConcreteLed(ILed led) {
        concreteLed = led;
		ledObserver.setLed(led) ;
	}
    @Override //IBlsFrameworkFsm
    public void addConcreteLed(ILed led) {
        ILedObserver ledObs = LedObserver.create();
        concreteLed = led;
        ledObs.setLed(led);
        ledmodel.addObserver( ledObs );
    }
	@Override //IBlsFrameworkFsm
	public void addConcreteButton(IObservable button) {
		//concreteButton = button;            //for a single controller
        button.addObserver(buttonmodel);  //STARTS
	}
	public void setApplLogic( IAppLogic appLogic ){
        applLogic.execute("stop");			//to promote applicationLogic
        applLogic = appLogic;
        applLogic.setControlled( ledmodel );
		buttonObserver.setControl( applLogic );
	}

 	/*
	 * Selectors
	 */
	@Override
	public ILedModel getLedModel() {
		return ledmodel;
	}
	@Override
	public IButtonModel getButtonModel() {
		return buttonmodel;
	}
	@Override
	public ILed getLedConcrete() {
		return concreteLed;
	}
	@Override
	public IObservable getButtonConcrete() {
		return concreteButton;
	}
	@Override
	public IAppLogic getApplLogic(){
		return applLogic;
	}
	@Override
	public IApplListener getButtonObserver(){
		return  buttonObserver;
	}

/*
----------------------------------------
MAIN
----------------------------------------
 */
	public static void main(String[] args) {
		IBlsFramework blSystem = MainBlsFramework.createTheSystem("BLSFramework");

		blSystem.setConcreteLed( LedAsGui.createLed() );
		//blSystem.addConcreteLed( LedAsGui.createLed() );
        //blSystem.addConcreteLed(LedProxyArduino.create(DeviceConfig.serialPortNum, DeviceConfig.serialBaudrate));
		blSystem.addConcreteButton( ButtonAsGui.createButton("LedControl") );
        //blSystem.addConcreteButton( new ButtonProxyArduino(DeviceConfig.serialPortNum, DeviceConfig.serialBaudrate));

		//Utils.delay(5000);
		//System.out.println(" ================== CHANGE CONTROL ================= ");
 		//blSystem.setApplLogic (  new AnotherApplLogic() );

 	}
}

/*
	//Inject the concrete MOCK devices
		blSystem.setConcreteLed( LedMock.createLed() );
		blSystem.setConcreteButton( ButtonMock.createButton() );

	//Inject the concrete Led Arduino devices
		blSystem.setConcreteLed(LedProxy.createLed(DeviceConfig.serialPortNum, DeviceConfig.serialBaudrate));
		blSystem.setConcreteButton(ButtonAsGui.createButton("ClickMe"));

	//Inject the concrete Button Arduino devices
		blSystem.setConcreteLed( LedGui.createLed() );
		blSystem.setConcreteButton( new ButtonProxyArduino(DeviceConfig.serialPortNum, DeviceConfig.serialBaudrate));

	//CHANGE THE SYSTEM DYNAMICALLY BY ADDING TO A GUI LED AN ARDUINO LED
		blSystem.setConcreteLed( LedGui.createLed() );
		blSystem.setConcreteButton(ButtonAsGui.createButton("ClickMe"));
		Utils.delay(5000);
		blSystem.setConcreteLed(LedProxy.createLed(DeviceConfig.serialPortNum, DeviceConfig.serialBaudrate));
*/

