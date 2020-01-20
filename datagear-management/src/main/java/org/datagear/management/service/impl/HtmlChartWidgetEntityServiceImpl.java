/*
 * Copyright (c) 2018 datagear.tech. All Rights Reserved.
 */

/**
 * 
 */
package org.datagear.management.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;
import org.datagear.analysis.ChartDataSet;
import org.datagear.analysis.ChartPlugin;
import org.datagear.analysis.ChartPluginManager;
import org.datagear.analysis.DataSet;
import org.datagear.analysis.RenderContext;
import org.datagear.analysis.support.ChartWidget;
import org.datagear.analysis.support.html.HtmlChartPlugin;
import org.datagear.analysis.support.html.HtmlRenderContext;
import org.datagear.management.domain.HtmlChartWidgetEntity;
import org.datagear.management.domain.User;
import org.datagear.management.service.AuthorizationService;
import org.datagear.management.service.HtmlChartWidgetEntityService;
import org.datagear.management.service.PermissionDeniedException;
import org.datagear.management.service.SqlDataSetEntityService;
import org.datagear.util.StringUtil;
import org.mybatis.spring.SqlSessionTemplate;

import com.alibaba.fastjson.JSON;

/**
 * {@linkplain HtmlChartWidgetEntityService}实现类。
 * 
 * @author datagear@163.com
 *
 */
public class HtmlChartWidgetEntityServiceImpl
		extends AbstractMybatisDataPermissionEntityService<String, HtmlChartWidgetEntity>
		implements HtmlChartWidgetEntityService
{
	protected static final String SQL_NAMESPACE = HtmlChartWidgetEntity.class.getName();

	protected static final String DATA_SIGN_SPLITTER = "__DSSP__";

	private ChartPluginManager chartPluginManager;

	private SqlDataSetEntityService sqlDataSetEntityService;

	private AuthorizationService authorizationService;

	public HtmlChartWidgetEntityServiceImpl()
	{
		super();
	}

	public HtmlChartWidgetEntityServiceImpl(SqlSessionFactory sqlSessionFactory, ChartPluginManager chartPluginManager,
			SqlDataSetEntityService sqlDataSetEntityService, AuthorizationService authorizationService)
	{
		super(sqlSessionFactory);
		this.chartPluginManager = chartPluginManager;
		this.sqlDataSetEntityService = sqlDataSetEntityService;
		this.authorizationService = authorizationService;
	}

	public HtmlChartWidgetEntityServiceImpl(SqlSessionTemplate sqlSessionTemplate,
			ChartPluginManager chartPluginManager, SqlDataSetEntityService sqlDataSetEntityService,
			AuthorizationService authorizationService)
	{
		super(sqlSessionTemplate);
		this.chartPluginManager = chartPluginManager;
		this.sqlDataSetEntityService = sqlDataSetEntityService;
		this.authorizationService = authorizationService;
	}

	public ChartPluginManager getChartPluginManager()
	{
		return chartPluginManager;
	}

	public void setChartPluginManager(ChartPluginManager chartPluginManager)
	{
		this.chartPluginManager = chartPluginManager;
	}

	public SqlDataSetEntityService getSqlDataSetEntityService()
	{
		return sqlDataSetEntityService;
	}

	public void setSqlDataSetEntityService(SqlDataSetEntityService sqlDataSetEntityService)
	{
		this.sqlDataSetEntityService = sqlDataSetEntityService;
	}

	public AuthorizationService getAuthorizationService()
	{
		return authorizationService;
	}

	public void setAuthorizationService(AuthorizationService authorizationService)
	{
		this.authorizationService = authorizationService;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends RenderContext> ChartWidget<T> getChartWidget(String id)
	{
		HtmlChartWidgetEntity entity = getById(id);

		if (entity == null)
			return null;

		return (ChartWidget<T>) entity;
	}

	@Override
	public String getResourceType()
	{
		return HtmlChartWidgetEntity.AUTHORIZATION_RESOURCE_TYPE;
	}

	@Override
	public HtmlChartWidgetEntity getByStringId(User user, String id) throws PermissionDeniedException
	{
		return super.getById(user, id);
	}

	@Override
	protected boolean add(HtmlChartWidgetEntity entity, Map<String, Object> params)
	{
		boolean success = super.add(entity, params);

		if (success)
			saveWidgetDataSetRelations(entity);

		return success;
	}

	@Override
	protected boolean update(HtmlChartWidgetEntity entity, Map<String, Object> params)
	{
		boolean success = super.update(entity, params);

		if (success)
			saveWidgetDataSetRelations(entity);

		return success;
	}

	@Override
	protected boolean deleteById(String id, Map<String, Object> params)
	{
		boolean deleted = super.deleteById(id, params);

		if (deleted)
			this.authorizationService.deleteByResource(HtmlChartWidgetEntity.AUTHORIZATION_RESOURCE_TYPE, id);

		return deleted;
	}

	protected void saveWidgetDataSetRelations(HtmlChartWidgetEntity entity)
	{
		deleteMybatis("deleteDataSetRelationById", entity.getId());

		List<WidgetDataSetRelation> relations = getWidgetDataSetRelations(entity);

		if (!relations.isEmpty())
		{
			for (WidgetDataSetRelation relation : relations)
				insertMybatis("insertDataSetRelation", relation);
		}
	}

	@Override
	protected void postProcessSelects(List<HtmlChartWidgetEntity> list)
	{
		// 查询操作仅用于展示，不必完全加载
		if (list == null)
			return;

		for (HtmlChartWidgetEntity e : list)
		{
			HtmlChartPlugin<HtmlRenderContext> plugin = e.getHtmlChartPlugin();

			if (plugin != null)
			{
				HtmlChartPlugin<HtmlRenderContext> full = getHtmlChartPlugin(plugin.getId());
				plugin.setNameLabel(full.getNameLabel());
				plugin.setDescLabel(full.getDescLabel());
			}
		}
	}

	@Override
	protected void postProcessSelect(HtmlChartWidgetEntity obj)
	{
		if (obj == null)
			return;

		setHtmlChartPlugin(obj);
		setChartDataSets(obj);
	}

	@Override
	protected void addDataPermissionParameters(Map<String, Object> params, User user)
	{
		addDataPermissionParameters(params, user, getResourceType(), false, true);
	}

	@Override
	protected String getSqlNamespace()
	{
		return SQL_NAMESPACE;
	}

	protected void setHtmlChartPlugin(HtmlChartWidgetEntity obj)
	{
		HtmlChartPlugin<HtmlRenderContext> htmlChartPlugin = obj.getHtmlChartPlugin();

		if (htmlChartPlugin != null)
		{
			htmlChartPlugin = getHtmlChartPlugin(htmlChartPlugin.getId());
			obj.setHtmlChartPlugin(htmlChartPlugin);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected HtmlChartPlugin<HtmlRenderContext> getHtmlChartPlugin(String id)
	{
		return (HtmlChartPlugin<HtmlRenderContext>) (ChartPlugin) this.chartPluginManager.get(id);
	}

	protected void setChartDataSets(HtmlChartWidgetEntity widget)
	{
		Map<String, Object> sqlParams = buildParamMapWithIdentifierQuoteParameter();
		sqlParams.put("widgetId", widget.getId());

		List<WidgetDataSetRelation> relations = selectListMybatis("getDataSetRelations", sqlParams);

		List<ChartDataSet> chartDataSets = new ArrayList<ChartDataSet>(relations.size());

		for (int i = 0; i < relations.size(); i++)
		{
			ChartDataSet chartDataSet = toChartDataSet(relations.get(i));

			if (chartDataSet != null)
				chartDataSets.add(chartDataSet);
		}

		widget.setChartDataSets(chartDataSets.toArray(new ChartDataSet[chartDataSets.size()]));
	}

	protected ChartDataSet toChartDataSet(WidgetDataSetRelation relation)
	{
		if (relation == null || StringUtil.isEmpty(relation.getDataSetId()))
			return null;

		DataSet dataSet = this.sqlDataSetEntityService.getSqlDataSet(relation.getDataSetId());

		if (dataSet == null)
			return null;

		ChartDataSet chartDataSet = new ChartDataSet(dataSet);
		chartDataSet.setPropertySigns(toPropertySigns(relation.getPropertySignsJson()));

		return chartDataSet;
	}

	protected Map<String, Set<String>> toPropertySigns(String json)
	{
		Map<String, Set<String>> propertySigns = new HashMap<String, Set<String>>();

		if (!StringUtil.isEmpty(json))
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> jsonMap = (Map<String, Object>) JSON.parse(json);

			for (Map.Entry<String, Object> entry : jsonMap.entrySet())
			{
				Set<String> signs = new HashSet<String>();

				Object valueObj = entry.getValue();

				if (valueObj instanceof String)
					signs.add((String) valueObj);
				else if (valueObj instanceof Collection<?>)
				{
					@SuppressWarnings("unchecked")
					Collection<String> valueCollection = (Collection<String>) valueObj;
					signs.addAll(valueCollection);
				}
				else if (valueObj instanceof Object[])
				{
					Object[] valueArray = (Object[]) valueObj;
					for (Object value : valueArray)
					{
						if (value instanceof String)
							signs.add((String) value);
					}
				}

				propertySigns.put(entry.getKey(), signs);
			}
		}

		return propertySigns;
	}

	protected List<WidgetDataSetRelation> getWidgetDataSetRelations(HtmlChartWidgetEntity obj)
	{
		List<WidgetDataSetRelation> list = new ArrayList<WidgetDataSetRelation>();

		if (obj == null)
			return list;

		ChartDataSet[] chartDataSets = obj.getChartDataSets();

		if (chartDataSets == null)
			return list;

		for (int i = 0; i < chartDataSets.length; i++)
		{
			ChartDataSet chartDataSet = chartDataSets[i];

			String propertySignsJson = "";
			if (chartDataSet.hasPropertySign())
				propertySignsJson = JSON.toJSONString(chartDataSet.getPropertySigns());

			WidgetDataSetRelation relation = new WidgetDataSetRelation(obj.getId(), chartDataSet.getDataSet().getId(),
					i + 1);
			relation.setPropertySignsJson(propertySignsJson);

			list.add(relation);
		}

		return list;
	}

	public static class WidgetDataSetRelation
	{
		private String widgetId;

		private String dataSetId;

		private String propertySignsJson;

		private int order;

		public WidgetDataSetRelation()
		{
			super();
		}

		public WidgetDataSetRelation(String widgetId, String dataSetId, int order)
		{
			super();
			this.widgetId = widgetId;
			this.dataSetId = dataSetId;
			this.order = order;
		}

		public String getWidgetId()
		{
			return widgetId;
		}

		public void setWidgetId(String widgetId)
		{
			this.widgetId = widgetId;
		}

		public String getDataSetId()
		{
			return dataSetId;
		}

		public void setDataSetId(String dataSetId)
		{
			this.dataSetId = dataSetId;
		}

		public String getPropertySignsJson()
		{
			return propertySignsJson;
		}

		public void setPropertySignsJson(String propertySignsJson)
		{
			this.propertySignsJson = propertySignsJson;
		}

		public int getOrder()
		{
			return order;
		}

		public void setOrder(int order)
		{
			this.order = order;
		}
	}
}
