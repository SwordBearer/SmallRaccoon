package xmu.swordbearer.smallraccoon.filter;

import java.io.IOException;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * 提取网页内容，但是图片显示还有问题
 * 
 * @author swordbearer
 * 
 */
public class HtmlFilter {
	public static final String ENCODE = "UTF-8";
	public static final int BUFFER_SIZE = 1024;

	public static void main(String[] args) throws IOException {
		// String uri =
		// "http://www.csdn.net/article/2012-12-23/2813067-lukuang-interview";
		// String result;
		// System.out.println("111");
		// HttpGetHelper httpGetHelper=new HttpGetHelper();
		// result = httpGetHelper.httpGetString(uri, null);
		// System.out.println("返回的结果是\n" + result);
		// System.out.println("===============================");
		//
		// try {
		// filterPage(result, ENCODE);
		// } catch (ParserException e) {
		// e.printStackTrace();
		// }
	}

	public void filterPage(String originalContent, String charset)
			throws ParserException {
		if (charset == null) {
			charset = "UTF-8";
		}
		Parser parser = Parser.createParser(originalContent, charset);
		TagNameFilter filter = new TagNameFilter("div");

		NodeList nodeList = parser.parse(filter);
		System.out.println(nodeList.size());
		Node node = nodeList.elementAt(18);
		NodeList list = node.getChildren();
		Node nodeTitle = list.elementAt(3);
		System.out.println(nodeTitle.getText());
	}
}
