package jp.co.internous.leo.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.co.internous.leo.model.domain.TblCart;
import jp.co.internous.leo.model.domain.dto.CartDto;
import jp.co.internous.leo.model.form.CartForm;
import jp.co.internous.leo.model.mapper.TblCartMapper;
import jp.co.internous.leo.model.session.LoginSession;

/**
 * カート情報に関する処理のコントローラー
 */
@Controller
@RequestMapping("/leo/cart")
public class CartController {
	
	@Autowired
	private LoginSession loginSession;

	@Autowired
	private TblCartMapper tblCartMapper;

	/**
	 * カート画面を初期表示する。
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/")
	public String index(Model m) {
		int userId = loginSession.getLoginFlag() ? loginSession.getUserId() : loginSession.getProvUserId();
		List<CartDto> cart = tblCartMapper.findByUserId(userId);
		m.addAttribute("cart", cart);
		m.addAttribute("loginSession", loginSession);
		return "cart";
	}
	
	/**
	 * カートに追加処理を行う
	 * @param form カート情報のForm
	 * @param m 画面表示用オブジェクト
	 * @return カート画面
	 */
	@RequestMapping("/add")
	public String addCart(CartForm form, Model m) {
		int userId = loginSession.getLoginFlag() ? loginSession.getUserId() : loginSession.getProvUserId();
		form.setUserId(userId);
		TblCart tblCart = new TblCart(form);
		tblCart.setUserId(userId);
		int result = 0;
		if (tblCartMapper.findCountByUserIdAndProuductId(userId, form.getProductId()) > 0) {
			result = tblCartMapper.update(tblCart);
		} else {
			result = tblCartMapper.insert(tblCart);
		}
		if (result > 0) {
			List<CartDto> cart = tblCartMapper.findByUserId(userId);
			m.addAttribute("cart", cart);
			m.addAttribute("loginSession", loginSession);
		}
		return "cart";
	}
	
	/**
	 * カート情報を削除する
	 * @param form CartFormのidArgフィールドで選択したカート情報のIDの配列を受け取る
	 * @return true:削除成功、false:削除失敗
	 */
	@ResponseBody
	@PostMapping("/delete")
	public boolean delete(@RequestBody CartForm form) {
		int result = 0;
		String[] idArg = form.getIdArg();
		List<String> idList = Arrays.asList(idArg);
		result = tblCartMapper.deleteById(idList);
		return result > 0;
	}
}