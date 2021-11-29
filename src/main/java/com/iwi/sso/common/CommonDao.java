package com.iwi.sso.common;

import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

@Repository("CommonDao")
public class CommonDao extends SqlSessionDaoSupport {

	@Resource(name = "sqlSession")
	public void setSqlSessionFactory(SqlSessionFactory sqlSession) {
		super.setSqlSessionFactory(sqlSession);
	}

	public int insert(String queryId, Object parameterObject) {
		return getSqlSession().insert(queryId, parameterObject);
	}

	public int update(String queryId, Object parameterObject) {
		return getSqlSession().update(queryId, parameterObject);
	}

	public int delete(String queryId, Object parameterObject) {
		return getSqlSession().delete(queryId, parameterObject);
	}

	public Object select(String queryId, Object parameterObject) {
		return getSqlSession().selectOne(queryId, parameterObject);
	}

	@SuppressWarnings("rawtypes")
	public List list(String queryId, Object parameterObject) {
		return getSqlSession().selectList(queryId, parameterObject);
	}

	@SuppressWarnings("rawtypes")
	public List list(String queryId) {
		return getSqlSession().selectList(queryId);
	}

	@SuppressWarnings("rawtypes")
	public List listPage(String queryId, Object parameterObject, int pageIndex, int pageSize) {
		int skipResults = pageIndex * pageSize;
		// int maxResults = (pageIndex * pageSize) + pageSize;

		RowBounds rowBounds = new RowBounds(skipResults, pageSize);

		return getSqlSession().selectList(queryId, parameterObject, rowBounds);
	}

}