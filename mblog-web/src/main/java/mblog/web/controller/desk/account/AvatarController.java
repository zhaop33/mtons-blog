/**
 * 
 */
package mblog.web.controller.desk.account;

import java.io.File;

import mtons.modules.pojos.Data;
import mtons.modules.utils.GMagickUtils;
import mtons.modules.utils.Text;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import mblog.core.context.AppContext;
import mblog.data.AccountProfile;
import mblog.lang.Consts;
import mblog.persist.service.UserService;
import mblog.web.controller.BaseController;
import mblog.web.controller.desk.Views;

/**
 * @author langhsu
 *
 */
@Controller
@RequestMapping("/account")
public class AvatarController extends BaseController {
	@Autowired
	private AppContext appContext;
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/avatar", method = RequestMethod.GET)
	public String view() {
		return getView(Views.ACCOUNT_AVATAR);
	}
	
	@RequestMapping(value = "/avatar", method = RequestMethod.POST)
	public String post(String path, Float x, Float y, Float width, Float height, ModelMap model) {
		AccountProfile profile = getSubject().getProfile();
		
		if (StringUtils.isEmpty(path)) {
			model.put("data", Data.failure("请选择图片"));
			return getView(Views.ACCOUNT_AVATAR);
		}
		
		if (width != null && height != null) {
			String root = getRealPath("/");
			File temp = new File(root + path);
			File scale = null;
			
			// 目标目录
			String ava100 = appContext.getAvaDir() + getAvaPath(profile.getId(), 100);
			String dest = root + ava100;
			try {
				// 判断父目录是否存在
				File f = new File(dest);
		        if(!f.getParentFile().exists()){
		            f.mkdirs();
		        }
		        // 在目标目录下生成截图
		        String scalePath = f.getParent() + "/" + profile.getId() + ".jpg";
				GMagickUtils.truncateImage(temp.getAbsolutePath(), scalePath, x.intValue(), y.intValue(), width.intValue());
				
				// 对结果图片进行压缩
				GMagickUtils.scaleImage(scalePath, dest, 100);
				
				AccountProfile user = userService.updateAvatar(profile.getId(), ava100);
				putProfile(user);
				
				scale = new File(scalePath);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				temp.delete();
				if (scale != null) {
					scale.delete();
				}
			}
		}
		return "redirect:/account/profile";
	}
	
	public String getAvaPath(long uid, int size) {
		String base = Text.filePath(uid, Consts.FILE_PATH_SEED, 2);
		return String.format("/%s/%d.jpg", base, size);
	}
}
