package jp.gaomar.mytem;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 商品を読み取った履歴です
 * 
 */
public class MytemHistory {
	/** JANコード*/
	private String janCode;
	/** 商品名*/
	private String itemName;
	/** 購入店 */
	private String shopName;
	/** 購入金額 */
	private int price;
	/** 購入日 */
	private Date postDate;
	/** 備考欄 */
	private String note;

	/**
	 * コンストラクタ
	 * 
	 * @param mytemMaster
	 */
	public MytemHistory(String _shopname, int _price, Date _postdate, String _note) {
		this.shopName = _shopname;
		this.price = _price;
		this.postDate = _postdate;
		this.note = _note;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param mytemMaster
	 */
	public MytemHistory(String _jancode, String _itemname, String _shopname, int _price, Date _postdate, String _note) {
		this.janCode = _jancode;
		this.itemName = _itemname;
		this.shopName = _shopname;
		this.price = _price;
		this.postDate = _postdate;
		this.note = _note;
	}

	public static SimpleDateFormat getSimpleDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd");
	}

	public String getJanCode() {
		return janCode;
	}

	public String getItemName() {
		return itemName;
	}
	
	public String getShopName() {
		return shopName;
	}

	public int getPrice() {
		return price;
	}

	public Date getPostDate() {
		return postDate;
	}

	public String getNote() {
		return note;
	}


}
