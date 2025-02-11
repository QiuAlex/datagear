/*
 * Copyright 2018 datagear.tech
 *
 * Licensed under the LGPLv3 license:
 * http://www.gnu.org/licenses/lgpl-3.0.html
 */

package org.datagear.analysis.support.html;

import java.io.IOException;
import java.io.Writer;

import org.datagear.analysis.ChartDataSet;
import org.datagear.analysis.DataSet;
import org.datagear.analysis.DataSetException;
import org.datagear.analysis.DataSetQuery;
import org.datagear.analysis.DataSetResult;
import org.datagear.analysis.support.AbstractDataSet;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * {@linkplain HtmlChart} JS脚本对象输出流。
 * 
 * @author datagear@163.com
 *
 */
public class HtmlChartScriptObjectWriter extends AbstractHtmlScriptObjectWriter
{
	public HtmlChartScriptObjectWriter()
	{
		super();
	}

	/**
	 * 将{@linkplain HtmlChart}的JS脚本对象写入输出流。
	 * <p>
	 * 格式为：
	 * </p>
	 * <code>
	 * <pre>
	 * var [chart.varName]=
	 * {
	 * 	...,
	 * 	renderContext : [renderContextVarName],
	 * 	plugin : [pluginVarName],
	 * 	...
	 * };
	 * <pre>
	 * </code>
	 * 
	 * @param out
	 * @param chart
	 * @param renderContextVarName
	 * @param pluginVarName
	 * @throws IOException
	 */
	public void write(Writer out, HtmlChart chart, String renderContextVarName, String pluginVarName) throws IOException
	{
		chart = new HtmlChartJson(chart, renderContextVarName, pluginVarName);

		out.write("var " + chart.getVarName() + "=");
		writeNewLine(out);
		writeJsonObject(out, chart);
		out.write(";");
		writeNewLine(out);
	}

	/**
	 * 将{@linkplain HtmlChart}的JSON对象写入输出流。
	 * <p>
	 * 格式为：
	 * </p>
	 * <code>
	 * <pre>
	 * {
	 * 	...,
	 * 	renderContext : [renderContextVarName],
	 * 	plugin : [pluginVarName],
	 * 	...
	 * }
	 * <pre>
	 * </code>
	 * 
	 * @param out
	 * @param chart
	 * @param renderContextVarName
	 * @param pluginVarName
	 * @throws IOException
	 */
	public void writeJson(Writer out, HtmlChart chart, String renderContextVarName, String pluginVarName)
			throws IOException
	{
		chart = new HtmlChartJson(chart, renderContextVarName, pluginVarName);

		writeJsonObject(out, chart);
	}

	/**
	 * 用于输出JSON的{@linkplain HtmlChart}。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected static class HtmlChartJson extends HtmlChart
	{
		private static final long serialVersionUID = 1L;

		public HtmlChartJson(HtmlChart htmlChart, String renderContextVarName, String pluginVarName)
		{
			super(htmlChart);
			setChartDataSets(ChartDataSetJson.valuesOf(htmlChart.getChartDataSets()));
			setPlugin(new RefHtmlChartPlugin(pluginVarName));
			setRenderContext(new RefRenderContext(renderContextVarName));
		}
	}

	/**
	 * 用于输出JSON的{@linkplain ChartDataSet}。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected static class ChartDataSetJson extends ChartDataSet
	{
		private static final long serialVersionUID = 1L;

		public ChartDataSetJson(ChartDataSet chartDataSet)
		{
			super(new DataSetJson(chartDataSet.getDataSet()));
			setPropertySigns(chartDataSet.getPropertySigns());
			setAlias(chartDataSet.getAlias());
			setAttachment(chartDataSet.isAttachment());
			setQuery(chartDataSet.getQuery());
		}

		@JsonIgnore
		@Override
		public DataSetResult getResult()
		{
			throw new UnsupportedOperationException();
		}

		public static ChartDataSetJson[] valuesOf(ChartDataSet[] chartDataSets)
		{
			if (chartDataSets == null)
				return null;

			ChartDataSetJson[] jsonDataSets = new ChartDataSetJson[chartDataSets.length];

			for (int i = 0; i < chartDataSets.length; i++)
				jsonDataSets[i] = new ChartDataSetJson(chartDataSets[i]);

			return jsonDataSets;
		}
	}

	/**
	 * 用于输出JSON的{@linkplain DataSet}。
	 * 
	 * @author datagear@163.com
	 *
	 */
	protected static class DataSetJson extends AbstractDataSet
	{
		private static final long serialVersionUID = 1L;

		public DataSetJson(DataSet dataSet)
		{
			super(dataSet.getId(), dataSet.getName(), dataSet.getProperties());
			setParams(dataSet.getParams());
			
			if(dataSet instanceof AbstractDataSet)
				setDataFormat(((AbstractDataSet) dataSet).getDataFormat());
		}

		@Override
		public DataSetResult getResult(DataSetQuery query) throws DataSetException
		{
			return null;
		}
	}
}
