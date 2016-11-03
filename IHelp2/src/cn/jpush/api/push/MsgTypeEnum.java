package cn.jpush.api.push;

public enum MsgTypeEnum {
	//闁氨鐓＄猾璇茬�濞戝牊浼�	NOTIFY(1),CUSTOM(2);
	NOTIFY(1),CUSTOM(2);
	
	//閻劍鍩涢懛顏勭暰娑斿琚崹瀣Х閹拷	CUSTOM(2);
	
	private final int value;
	
	private MsgTypeEnum(final int value) {
		this.value = value;
	}
	public int value() {
		return this.value;
	}
}