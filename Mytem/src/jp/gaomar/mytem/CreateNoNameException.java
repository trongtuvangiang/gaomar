package jp.gaomar.mytem;
/**
 * 登録するときに商品名がセットされてないときのexceptionです
 * @author otori
 *
 */
public class CreateNoNameException extends Exception {

	private static final long serialVersionUID = -4808215196788142640L;

	public CreateNoNameException(){
		
	}
	
	/**
	 * コンストラクタ
	 * @param throwable
	 */
	public CreateNoNameException(Throwable throwable){
		super(throwable);
	}
	
}
