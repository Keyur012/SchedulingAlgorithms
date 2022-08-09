package example;


public class Pair<T> {
	private T st;
	private T en;
	
	public Pair(T st, T en) {
		this.setSt(st);
		this.setEn(en);
	}

	public T getSt() {
		return st;
	}

	public void setSt(T st) {
		this.st = st;
	}

	public T getEn() {
		return en;
	}

	public void setEn(T en) {
		this.en = en;
	}	
}
