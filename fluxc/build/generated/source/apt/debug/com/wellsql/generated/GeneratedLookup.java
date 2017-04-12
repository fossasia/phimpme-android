package com.wellsql.generated;

import com.yarolegovich.wellsql.core.Mapper;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.core.TableLookup;
import java.lang.Class;
import java.lang.Override;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.wordpress.android.fluxc.model.AccountModel;
import org.wordpress.android.fluxc.model.CommentModel;
import org.wordpress.android.fluxc.model.MediaModel;
import org.wordpress.android.fluxc.model.PostFormatModel;
import org.wordpress.android.fluxc.model.PostModel;
import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.fluxc.model.TaxonomyModel;
import org.wordpress.android.fluxc.model.TermModel;
import org.wordpress.android.fluxc.network.HTTPAuthModel;

public final class GeneratedLookup implements TableLookup {
  private final Map<Class<?>, TableClass> tables;

  private final Map<Class<?>, Mapper<?>> mappers;

  public GeneratedLookup() {
    tables = new HashMap<Class<?>, TableClass>();
    mappers = new HashMap<Class<?>, Mapper<?>>();
    tables.put(AccountModel.class, new com.wellsql.generated.AccountModelTable());
    mappers.put(AccountModel.class, new com.wellsql.generated.AccountModelMapper());
    tables.put(SiteModel.class, new com.wellsql.generated.SiteModelTable());
    mappers.put(SiteModel.class, new com.wellsql.generated.SiteModelMapper());
    tables.put(TaxonomyModel.class, new com.wellsql.generated.TaxonomyModelTable());
    mappers.put(TaxonomyModel.class, new com.wellsql.generated.TaxonomyModelMapper());
    tables.put(TermModel.class, new com.wellsql.generated.TermModelTable());
    mappers.put(TermModel.class, new com.wellsql.generated.TermModelMapper());
    tables.put(MediaModel.class, new com.wellsql.generated.MediaModelTable());
    mappers.put(MediaModel.class, new com.wellsql.generated.MediaModelMapper());
    tables.put(PostModel.class, new com.wellsql.generated.PostModelTable());
    mappers.put(PostModel.class, new com.wellsql.generated.PostModelMapper());
    tables.put(CommentModel.class, new com.wellsql.generated.CommentModelTable());
    mappers.put(CommentModel.class, new com.wellsql.generated.CommentModelMapper());
    tables.put(PostFormatModel.class, new com.wellsql.generated.PostFormatModelTable());
    mappers.put(PostFormatModel.class, new com.wellsql.generated.PostFormatModelMapper());
    tables.put(HTTPAuthModel.class, new com.wellsql.generated.HTTPAuthModelTable());
    mappers.put(HTTPAuthModel.class, new com.wellsql.generated.HTTPAuthModelMapper());
  }

  @Override
  public Set<Class<?>> getTableTokens() {
    return tables.keySet();
  }

  @Override
  public Set<Class<?>> getMapperTokens() {
    return mappers.keySet();
  }

  @Override
  public TableClass getTable(Class<?> token) {
    return tables.get(token);
  }

  @Override
  public <T> Mapper<T> getMapper(Class<T> token) {
    return (Mapper<T>) mappers.get(token);
  }
}
