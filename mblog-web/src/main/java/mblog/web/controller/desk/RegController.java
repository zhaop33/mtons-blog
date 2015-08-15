/**
 * 
 */
package mblog.web.controller.desk;

import mblog.data.User;
import mblog.extend.data.DataExt;
import mblog.extend.email.EmailSender;
import mblog.lang.Consts;
import mblog.persist.service.UserService;
import mblog.persist.service.VerifyService;
import mblog.web.controller.BaseController;
import mtons.modules.pojos.Data;
import mtons.modules.pojos.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @author langhsu
 *
 */
@Controller
public class RegController extends BaseController {
	@Autowired
	private UserService userService;
	@Autowired
	private VerifyService verifyService;
	@Autowired
	private EmailSender emailSender;
	
	@RequestMapping(value = "/reg", method = RequestMethod.GET)
	public String view(ModelMap model) {
		UserProfile profile = getSubject().getProfile();
		if (profile != null) {
			return "redirect:/home";
		}
		return getView(Views.REG);
	}
	
	@RequestMapping(value = "/reg", method = RequestMethod.POST)
	public String reg(User post, ModelMap model) {
		DataExt data;
		String ret = getView(Views.REG);
		
		try {
			post.setAvatar(Consts.AVATAR);
			User user = userService.register(post);

			sendEmail(user);

			data = DataExt.success("恭喜您! 注册成功, 已经给您的邮箱发了验证码, 赶紧去完成邮箱绑定吧。", Data.NOOP);
			data.addLink("login", "先去登陆尝尝鲜");

			ret = getView(Views.REG_RESULT);
			
		} catch (Exception e) {
			data = DataExt.failure(e.getMessage());
		}
		model.put("data", data);
		return ret;
	}

	private void sendEmail(User user) {
		String code = verifyService.generateCode(user.getId(), Consts.VERIFY_BIND, user.getEmail());
		Map<String, Object> data = new HashMap<>();
		data.put("userId", user.getId());
		data.put("code", code);
		data.put("type", Consts.VERIFY_BIND);

		emailSender.to("bind.vm", user.getEmail(), "邮箱绑定验证", data);
	}

}
