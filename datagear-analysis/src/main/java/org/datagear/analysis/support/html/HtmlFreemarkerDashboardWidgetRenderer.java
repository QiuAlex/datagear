/*
 * Copyright (c) 2018 datagear.tech. All Rights Reserved.
 */

/**
 * 
 */
package org.datagear.analysis.support.html;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datagear.analysis.Chart;
import org.datagear.analysis.ChartTheme;
import org.datagear.analysis.Dashboard;
import org.datagear.analysis.DashboardTheme;
import org.datagear.analysis.DashboardThemeSource;
import org.datagear.analysis.RenderContext;
import org.datagear.analysis.RenderException;
import org.datagear.analysis.RenderStyle;
import org.datagear.analysis.Theme;
import org.datagear.analysis.support.ChartWidget;
import org.datagear.analysis.support.ChartWidgetSource;
import org.datagear.analysis.support.SimpleDashboardThemeSource;
import org.datagear.analysis.support.TemplateDashboardWidgetResManager;
import org.datagear.util.IDUtil;
import org.datagear.util.IOUtil;
import org.datagear.util.StringUtil;

import freemarker.core.Environment;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 * {@linkplain HtmlFreemarkerDashboardWidget}渲染器。
 * <p>
 * 此类需要手动调用{@linkplain #init()}方法进行初始化。
 * </p>
 * 
 * @author datagear@163.com
 *
 * @param <T>
 */
public class HtmlFreemarkerDashboardWidgetRenderer<T extends HtmlRenderContext>
{
	public static final String DIRECTIVE_IMPORT = "import";

	public static final String DIRECTIVE_RESOURCE = "resource";

	public static final String DIRECTIVE_DASHBOARD = "dashboard";

	public static final String DIRECTIVE_CHART = "chart";

	public static final String DASHBOARD_ELEMENT_STYLE_NAME = "dashboard";

	public static final String CHART_ELEMENT_WRAPPER_STYLE_NAME = "chart-wrapper";

	protected static final String KEY_HTML_DASHBOARD_RENDER_DATA_MODEL = HtmlDashboardRenderDataModel.class
			.getSimpleName();

	/** "@import"指令的输出内容 */
	private String importContent;

	/** "@resource"指令的加载资根URL */
	private String resourceRootURL;

	private TemplateDashboardWidgetResManager templateDashboardWidgetResManager;

	private ChartWidgetSource chartWidgetSource;

	private DashboardThemeSource dashboardThemeSource = new SimpleDashboardThemeSource();

	private HtmlDashboardScriptObjectWriter htmlDashboardScriptObjectWriter = new HtmlDashboardScriptObjectWriter();

	private String templateEncoding = "UTF-8";

	private String writerEncoding = "UTF-8";

	/** 换行符 */
	private String newLine = "\r\n";

	private boolean ignoreDashboardStyleBorderWidth = true;

	private Configuration _configuration;

	public HtmlFreemarkerDashboardWidgetRenderer()
	{
		super();
	}

	public HtmlFreemarkerDashboardWidgetRenderer(String importContent, String resourceRootURL,
			TemplateDashboardWidgetResManager templateDashboardWidgetResManager, ChartWidgetSource chartWidgetSource)
	{
		super();
		this.importContent = importContent;
		this.resourceRootURL = resourceRootURL;
		this.templateDashboardWidgetResManager = templateDashboardWidgetResManager;
		this.chartWidgetSource = chartWidgetSource;
	}

	public String getImportContent()
	{
		return importContent;
	}

	public void setImportContent(String importContent)
	{
		this.importContent = importContent;
	}

	public String getResourceRootURL()
	{
		return resourceRootURL;
	}

	public void setResourceRootURL(String resourceRootURL)
	{
		this.resourceRootURL = resourceRootURL;
	}

	public TemplateDashboardWidgetResManager getTemplateDashboardWidgetResManager()
	{
		return templateDashboardWidgetResManager;
	}

	public void setTemplateDashboardWidgetResManager(
			TemplateDashboardWidgetResManager templateDashboardWidgetResManager)
	{
		this.templateDashboardWidgetResManager = templateDashboardWidgetResManager;
	}

	public ChartWidgetSource getChartWidgetSource()
	{
		return chartWidgetSource;
	}

	public void setChartWidgetSource(ChartWidgetSource chartWidgetSource)
	{
		this.chartWidgetSource = chartWidgetSource;
	}

	public DashboardThemeSource getDashboardThemeSource()
	{
		return dashboardThemeSource;
	}

	public void setDashboardThemeSource(DashboardThemeSource dashboardThemeSource)
	{
		this.dashboardThemeSource = dashboardThemeSource;
	}

	public HtmlDashboardScriptObjectWriter getHtmlDashboardScriptObjectWriter()
	{
		return htmlDashboardScriptObjectWriter;
	}

	public void setHtmlDashboardScriptObjectWriter(HtmlDashboardScriptObjectWriter htmlDashboardScriptObjectWriter)
	{
		this.htmlDashboardScriptObjectWriter = htmlDashboardScriptObjectWriter;
	}

	public String getTemplateEncoding()
	{
		return templateEncoding;
	}

	public void setTemplateEncoding(String templateEncoding)
	{
		this.templateEncoding = templateEncoding;
	}

	public String getWriterEncoding()
	{
		return writerEncoding;
	}

	public void setWriterEncoding(String writerEncoding)
	{
		this.writerEncoding = writerEncoding;
	}

	public String getNewLine()
	{
		return newLine;
	}

	public void setNewLine(String newLine)
	{
		this.newLine = newLine;
	}

	public boolean isIgnoreDashboardStyleBorderWidth()
	{
		return ignoreDashboardStyleBorderWidth;
	}

	public void setIgnoreDashboardStyleBorderWidth(boolean ignoreDashboardStyleBorderWidth)
	{
		this.ignoreDashboardStyleBorderWidth = ignoreDashboardStyleBorderWidth;
	}

	/**
	 * 初始化。
	 * 
	 * @throws IOException
	 */
	public void init() throws IOException
	{
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
		cfg.setDirectoryForTemplateLoading(this.templateDashboardWidgetResManager.getRootDirectory());
		cfg.setDefaultEncoding(this.templateEncoding);
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);

		cfg.setSharedVariable(DIRECTIVE_IMPORT, new ImportTemplateDirectiveModel());
		cfg.setSharedVariable(DIRECTIVE_RESOURCE, new ResourceTemplateDirectiveModel());
		cfg.setSharedVariable(DIRECTIVE_DASHBOARD, new DashboardTemplateDirectiveModel());
		cfg.setSharedVariable(DIRECTIVE_CHART, new ChartTemplateDirectiveModel());

		setConfiguration(cfg);
	}

	/**
	 * 渲染{@linkplain Dashboard}。
	 * 
	 * @param renderContext
	 * @param dashboardWidget
	 * @return
	 * @throws RenderException
	 */
	public HtmlDashboard render(T renderContext, HtmlFreemarkerDashboardWidget<T> dashboardWidget)
			throws RenderException
	{
		inflateThemes(renderContext);

		Template template = getTemplate(dashboardWidget);

		HtmlDashboard dashboard = new HtmlDashboard();

		dashboard.setId(IDUtil.uuid());
		dashboard.setWidget(dashboardWidget);
		dashboard.setRenderContext(renderContext);
		dashboard.setCharts(new ArrayList<Chart>());

		HtmlDashboardRenderDataModel dataModel = new HtmlDashboardRenderDataModel(dashboard);

		try
		{
			template.process(buildHtmlDashboardRenderDataModel(dataModel), renderContext.getWriter());
		}
		catch (Throwable t)
		{
			throw new RenderException(t);
		}

		return dashboard;
	}

	protected void inflateThemes(HtmlRenderContext renderContext)
	{
		RenderStyle renderStyle = HtmlRenderAttributes.getRenderStyle(renderContext);

		if (renderStyle == null)
		{
			renderStyle = RenderStyle.LIGHT;
			HtmlRenderAttributes.setRenderStyle(renderContext, renderStyle);
		}

		DashboardTheme dashboardTheme = HtmlRenderAttributes.getDashboardTheme(renderContext);

		if (dashboardTheme == null)
		{
			dashboardTheme = this.dashboardThemeSource.getDashboardTheme(renderStyle);

			if (dashboardTheme == null)
				dashboardTheme = SimpleDashboardThemeSource.THEME_LIGHT;

			HtmlRenderAttributes.setDashboardTheme(renderContext, dashboardTheme);
		}

		ChartTheme chartTheme = HtmlRenderAttributes.getChartTheme(renderContext);

		if (chartTheme == null)
		{
			chartTheme = dashboardTheme.getChartTheme();
			HtmlRenderAttributes.setChartTheme(renderContext, chartTheme);
		}
	}

	/**
	 * 获取{@linkplain HtmlFreemarkerDashboardWidget#getId()}的指定模板对象。
	 * 
	 * @param dashboardWidget
	 * @return
	 * @throws RenderException
	 */
	protected Template getTemplate(HtmlFreemarkerDashboardWidget<T> dashboardWidget) throws RenderException
	{
		String path = this.templateDashboardWidgetResManager.getTemplateRelativePath(dashboardWidget.getId(),
				dashboardWidget.getTemplate());

		try
		{
			return getConfiguration().getTemplate(path);
		}
		catch (Throwable t)
		{
			throw new RenderException(t);
		}
	}

	protected Configuration getConfiguration()
	{
		return _configuration;
	}

	protected void setConfiguration(Configuration _configuration)
	{
		this._configuration = _configuration;
	}

	/**
	 * 获取指定ID的{@linkplain ChartWidget}。
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected HtmlChartWidget<T> getHtmlChartWidget(String id)
	{
		return (HtmlChartWidget<T>) this.chartWidgetSource.getChartWidget(id);
	}

	protected Object buildHtmlDashboardRenderDataModel(HtmlDashboardRenderDataModel dataModel)
	{
		Map<String, Object> map = new HashMap<String, Object>();

		map.put(KEY_HTML_DASHBOARD_RENDER_DATA_MODEL, dataModel);

		return map;
	}

	protected HtmlDashboardRenderDataModel getHtmlDashboardRenderDataModel(Environment env)
			throws TemplateModelException
	{
		TemplateHashModel templateHashModel = env.getDataModel();
		HtmlDashboardRenderDataModel dataModel = (HtmlDashboardRenderDataModel) templateHashModel
				.get(KEY_HTML_DASHBOARD_RENDER_DATA_MODEL);

		return dataModel;
	}

	/**
	 * HTML看板渲染数据模型。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected static class HtmlDashboardRenderDataModel implements WrapperTemplateModel
	{
		private HtmlDashboard htmlDashboard;

		public HtmlDashboardRenderDataModel()
		{
			super();
		}

		public HtmlDashboardRenderDataModel(HtmlDashboard htmlDashboard)
		{
			super();
			this.htmlDashboard = htmlDashboard;
		}

		public HtmlDashboard getHtmlDashboard()
		{
			return htmlDashboard;
		}

		public void setHtmlDashboard(HtmlDashboard htmlDashboard)
		{
			this.htmlDashboard = htmlDashboard;
		}

		@Override
		public Object getWrappedObject()
		{
			return this.htmlDashboard;
		}
	}

	protected abstract class AbstractTemplateDirectiveModel implements TemplateDirectiveModel
	{
		public AbstractTemplateDirectiveModel()
		{
			super();
		}

		/**
		 * 获取字符串参数值。
		 * 
		 * @param params
		 * @param key
		 * @return
		 * @throws TemplateModelException
		 */
		protected String getStringParamValue(Map<?, ?> params, String key) throws TemplateModelException
		{
			Object value = params.get(key);

			if (value == null)
				return null;
			else if (value instanceof String)
				return (String) value;
			else if (value instanceof TemplateScalarModel)
				return ((TemplateScalarModel) value).getAsString();
			else
				throw new TemplateModelException(
						"Can not get string from [" + value.getClass().getName() + "] instance");
		}

		/**
		 * 写脚本开始标签。
		 * 
		 * @param out
		 * @throws IOException
		 */
		protected void writeScriptStartTag(Writer out) throws IOException
		{
			out.write("<script type=\"text/javascript\">");
		}

		/**
		 * 写脚本结束标签。
		 * 
		 * @param out
		 * @throws IOException
		 */
		protected void writeScriptEndTag(Writer out) throws IOException
		{
			out.write("</script>");
		}

		/**
		 * 写换行符。
		 * 
		 * @param out
		 * @throws IOException
		 */
		protected void writeNewLine(Writer out) throws IOException
		{
			out.write(getNewLine());
		}
	}

	/**
	 * “@import”指令。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected class ImportTemplateDirectiveModel extends AbstractTemplateDirectiveModel
	{
		public ImportTemplateDirectiveModel()
		{
			super();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
				throws TemplateException, IOException
		{
			HtmlDashboardRenderDataModel dataModel = getHtmlDashboardRenderDataModel(env);
			HtmlRenderContext renderContext = dataModel.getHtmlDashboard().getRenderContext();

			Writer out = env.getOut();

			out.write("<meta charset=\"" + getWriterEncoding() + "\">");
			writeNewLine(out);
			out.write(getImportContent());
			writeNewLine(out);

			DashboardTheme dashboardTheme = getDashboardTheme(renderContext);
			writeDashboardTheme(out, dashboardTheme);
		}

		protected DashboardTheme getDashboardTheme(RenderContext renderContext)
		{
			DashboardTheme dashboardTheme = HtmlRenderAttributes.getDashboardTheme(renderContext);
			return dashboardTheme;
		}

		protected void writeDashboardTheme(Writer out, DashboardTheme dashboardTheme) throws IOException
		{
			ChartTheme chartTheme = (dashboardTheme == null ? null : dashboardTheme.getChartTheme());

			out.write("<style type=\"text/css\">");
			writeNewLine(out);
			out.write("body{");
			writeNewLine(out);
			out.write("    padding: 0px 0px;");
			writeNewLine(out);
			out.write("    margin: 0px 0px;");
			writeNewLine(out);
			if (dashboardTheme != null)
			{
				out.write("    background-color: " + dashboardTheme.getBackgroundColor() + ";");
				writeNewLine(out);
			}
			out.write("}");
			writeNewLine(out);
			out.write("." + DASHBOARD_ELEMENT_STYLE_NAME + "{");
			writeNewLine(out);
			writeThemeCssAttrs(out, dashboardTheme);
			writeBorderBoxCssAttrs(out);
			if (isIgnoreDashboardStyleBorderWidth())
			{
				out.write("    border-width: 0px;");
				writeNewLine(out);
			}
			out.write("}");
			writeNewLine(out);

			out.write("." + CHART_ELEMENT_WRAPPER_STYLE_NAME + "{");
			writeNewLine(out);
			out.write("    position: relative;");
			writeNewLine(out);
			writeThemeCssAttrs(out, chartTheme);
			writeBorderBoxCssAttrs(out);
			out.write("}");
			writeNewLine(out);

			out.write("." + HtmlChartPlugin.BUILTIN_CHART_ELEMENT_STYLE_NAME + "{");
			writeNewLine(out);
			writeFillParentCssAttrs(out);
			writeBorderBoxCssAttrs(out);
			out.write("}");
			writeNewLine(out);

			out.write("</style>");
			writeNewLine(out);
		}

		protected void writeThemeCssAttrs(Writer out, Theme theme) throws IOException
		{
			if (theme != null)
			{
				String borderWidth = theme.getBorderWidth();
				if (borderWidth == null)
					borderWidth = "0";

				out.write("    color: " + theme.getForegroundColor() + ";");
				writeNewLine(out);
				out.write("    background-color: " + theme.getBackgroundColor() + ";");
				writeNewLine(out);
				out.write("    border-color: " + theme.getBorderColor() + ";");
				writeNewLine(out);
				out.write("    border-width: " + theme.getBorderWidth() + ";");
				writeNewLine(out);
				out.write("    border-style: solid;");
				writeNewLine(out);
			}
		}

		protected void writeFillParentCssAttrs(Writer out) throws IOException
		{
			out.write("    position: absolute;");
			writeNewLine(out);
			out.write("    top: 0px;");
			writeNewLine(out);
			out.write("    bottom: 0px;");
			writeNewLine(out);
			out.write("    left: 0px;");
			writeNewLine(out);
			out.write("    right: 0px;");
			writeNewLine(out);
		}

		protected void writeBorderBoxCssAttrs(Writer out) throws IOException
		{
			out.write("    box-sizing: border-box;");
			writeNewLine(out);
			out.write("    -moz-box-sizing: border-box;");
			writeNewLine(out);
			out.write("    -webkit-box-sizing: border-box;");
			writeNewLine(out);
		}
	}

	/**
	 * “@resource”指令。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected class ResourceTemplateDirectiveModel extends AbstractTemplateDirectiveModel
	{
		public ResourceTemplateDirectiveModel()
		{
			super();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
				throws TemplateException, IOException
		{
			String resName = getStringParamValue(params, "name");

			HtmlDashboardRenderDataModel dataModel = getHtmlDashboardRenderDataModel(env);

			if (StringUtil.isEmpty(resName))
				throw new TemplateException("The [name] attribute must be set", env);

			String widgetId = dataModel.getHtmlDashboard().getWidget().getId();

			String resURL = IOUtil.concatPath(widgetId, resName, "/");
			resURL = IOUtil.concatPath(getResourceRootURL(), resURL, "/");

			env.getOut().write(resURL);
		}
	}

	/**
	 * “@dashboard”指令。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected class DashboardTemplateDirectiveModel extends AbstractTemplateDirectiveModel
	{
		public DashboardTemplateDirectiveModel()
		{
			super();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
				throws TemplateException, IOException
		{
			String varName = getStringParamValue(params, "var");
			String listener = getStringParamValue(params, "listener");

			HtmlDashboardRenderDataModel dataModel = getHtmlDashboardRenderDataModel(env);
			HtmlRenderContext renderContext = dataModel.getHtmlDashboard().getRenderContext();

			if (StringUtil.isEmpty(varName))
				varName = HtmlRenderAttributes.generateDashboardVarName();

			dataModel.getHtmlDashboard().setVarName(varName);

			Writer out = env.getOut();

			writeScriptStartTag(out);
			writeNewLine(out);

			out.write("var ");
			out.write(varName);
			out.write("=");
			writeNewLine(out);
			writeHtmlDashboardScriptObject(out, dataModel.getHtmlDashboard());
			out.write(";");
			writeNewLine(out);

			out.write(varName
					+ ".render = function(){ for(var i=0; i<this.charts.length; i++){ this.charts[i].render(); } };");
			writeNewLine(out);

			out.write("window.onload = function(){");
			writeNewLine(out);
			if (!StringUtil.isEmpty(listener))
			{
				out.write(varName + ".listener = window[\"" + listener + "\"];");
				writeNewLine(out);
			}
			out.write("  if(" + varName + ".listener && " + varName + ".listener.beforeRender) " + varName
					+ ".listener.beforeRender(" + varName + "); ");
			writeNewLine(out);
			out.write("  " + varName + ".render();");
			writeNewLine(out);
			out.write("  if(" + varName + ".listener && " + varName + ".listener.afterRender) " + varName
					+ ".listener.afterRender(" + varName + "); ");
			writeNewLine(out);
			out.write("};");
			writeNewLine(out);

			writeScriptEndTag(out);
			writeNewLine(out);

			HtmlRenderAttributes.setChartRenderContextVarName(renderContext, varName + ".renderContext");
		}

		protected void writeHtmlDashboardScriptObject(Writer out, HtmlDashboard dashboard) throws IOException
		{
			getHtmlDashboardScriptObjectWriter().write(out, dashboard);
		}
	}

	/**
	 * “@chart”指令。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected class ChartTemplateDirectiveModel extends AbstractTemplateDirectiveModel
	{
		public ChartTemplateDirectiveModel()
		{
			super();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
				throws TemplateException, IOException
		{
			String widget = getStringParamValue(params, "widget");
			String var = getStringParamValue(params, "var");
			String elementId = getStringParamValue(params, "elementId");

			if (StringUtil.isEmpty(widget))
				throw new TemplateException("The [widget] attribute must be set", env);

			if (StringUtil.isEmpty(var))
				var = HtmlRenderAttributes.generateChartVarName();

			if (StringUtil.isEmpty(elementId))
				elementId = HtmlRenderAttributes.generateChartElementId();

			HtmlDashboardRenderDataModel dataModel = getHtmlDashboardRenderDataModel(env);
			HtmlDashboard htmlDashboard = dataModel.getHtmlDashboard();
			String dashboardVarName = htmlDashboard.getVarName();
			HtmlRenderContext renderContext = htmlDashboard.getRenderContext();
			HtmlChartWidget<T> chartWidget = getHtmlChartWidget(widget);

			HtmlRenderAttributes.setChartNotRenderScriptTag(renderContext, false);
			HtmlRenderAttributes.setChartScriptNotInvokeRender(renderContext, true);
			HtmlRenderAttributes.setChartVarName(renderContext, var);
			HtmlRenderAttributes.setChartElementId(renderContext, elementId);

			Chart chart = chartWidget.render((T) renderContext);

			Writer out = env.getOut();

			writeScriptStartTag(out);
			writeNewLine(out);
			out.write(dashboardVarName + ".charts.push(" + var + ");");
			writeNewLine(out);
			writeScriptEndTag(out);
			writeNewLine(out);

			List<Chart> charts = htmlDashboard.getCharts();
			if (charts == null)
			{
				charts = new ArrayList<Chart>();
				htmlDashboard.setCharts(charts);
			}

			charts.add(chart);
		}

		/**
		 * 写脚本开始标签。
		 * 
		 * @param out
		 * @throws IOException
		 */
		@Override
		protected void writeScriptStartTag(Writer out) throws IOException
		{
			out.write("<script type=\"text/javascript\">");
		}

		/**
		 * 写脚本结束标签。
		 * 
		 * @param out
		 * @throws IOException
		 */
		@Override
		protected void writeScriptEndTag(Writer out) throws IOException
		{
			out.write("</script>");
		}

		/**
		 * 写换行符。
		 * 
		 * @param out
		 * @throws IOException
		 */
		@Override
		protected void writeNewLine(Writer out) throws IOException
		{
			out.write(getNewLine());
		}
	}
}
