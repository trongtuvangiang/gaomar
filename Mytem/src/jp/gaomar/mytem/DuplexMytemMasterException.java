package jp.gaomar.mytem;

/**
 * 商品マスタが重複している場合のExceptionです
 * @author hide
 *
 */
public class DuplexMytemMasterException extends GaeException {

	private static final long serialVersionUID = 2332994598354016927L;
	
	public DuplexMytemMasterException(){
		
	}
	
	/**
	 * コンストラクタ
	 * @param throwable
	 */
	public DuplexMytemMasterException(Throwable throwable) {
		super(throwable);
	}


}
