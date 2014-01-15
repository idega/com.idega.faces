package com.idega.faces.componentbased;

import java.io.IOException;
import java.io.Writer;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.presentation.util.RenderUtil;
import com.idega.util.CoreConstants;
import com.idega.util.StringHandler;
import com.idega.util.StringUtil;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class FacesRenderUtil implements RenderUtil {

	@Override
	public void doRemoveNeedlessContentAndSetRealPageTitle(Writer writer, String newTitle, String oldTitle) {
		if (writer instanceof HtmlStringBufferedResponseWriter) {
			String html = writer.toString();
			if (StringUtil.isEmpty(html)) {
				return;
			}

			boolean changed = false;
			if (html.indexOf("<?xml version=") != -1) {
				html = StringHandler.replace(html, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>", CoreConstants.EMPTY);
				changed = true;
			}

			if (!StringUtil.isEmpty(newTitle) && !StringUtil.isEmpty(oldTitle) && !newTitle.equals(oldTitle)) {
				html = StringHandler.replace(html, "<title>" + oldTitle + "</title>", "<title>" + newTitle + "</title>");
				changed = true;
			}

			if (changed) {
				try {
					((HtmlStringBufferedResponseWriter) writer).setContent(html);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}