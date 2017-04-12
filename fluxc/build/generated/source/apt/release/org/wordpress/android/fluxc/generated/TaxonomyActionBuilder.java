package org.wordpress.android.fluxc.generated;

import java.lang.Void;
import org.wordpress.android.fluxc.action.TaxonomyAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.fluxc.model.TermModel;
import org.wordpress.android.fluxc.store.TaxonomyStore;

public final class TaxonomyActionBuilder extends ActionBuilder {
  public static Action<SiteModel> newFetchCategoriesAction(SiteModel payload) {
    return new Action<>(TaxonomyAction.FETCH_CATEGORIES, payload);
  }

  public static Action<SiteModel> newFetchTagsAction(SiteModel payload) {
    return new Action<>(TaxonomyAction.FETCH_TAGS, payload);
  }

  public static Action<TaxonomyStore.FetchTermsPayload> newFetchTermsAction(TaxonomyStore.FetchTermsPayload payload) {
    return new Action<>(TaxonomyAction.FETCH_TERMS, payload);
  }

  public static Action<TaxonomyStore.RemoteTermPayload> newFetchTermAction(TaxonomyStore.RemoteTermPayload payload) {
    return new Action<>(TaxonomyAction.FETCH_TERM, payload);
  }

  public static Action<TaxonomyStore.RemoteTermPayload> newPushTermAction(TaxonomyStore.RemoteTermPayload payload) {
    return new Action<>(TaxonomyAction.PUSH_TERM, payload);
  }

  public static Action<TaxonomyStore.FetchTermsResponsePayload> newFetchedTermsAction(TaxonomyStore.FetchTermsResponsePayload payload) {
    return new Action<>(TaxonomyAction.FETCHED_TERMS, payload);
  }

  public static Action<TaxonomyStore.FetchTermResponsePayload> newFetchedTermAction(TaxonomyStore.FetchTermResponsePayload payload) {
    return new Action<>(TaxonomyAction.FETCHED_TERM, payload);
  }

  public static Action<TaxonomyStore.RemoteTermPayload> newPushedTermAction(TaxonomyStore.RemoteTermPayload payload) {
    return new Action<>(TaxonomyAction.PUSHED_TERM, payload);
  }

  public static Action<TermModel> newUpdateTermAction(TermModel payload) {
    return new Action<>(TaxonomyAction.UPDATE_TERM, payload);
  }

  public static Action<Void> newRemoveAllTermsAction() {
    return generateNoPayloadAction(TaxonomyAction.REMOVE_ALL_TERMS);
  }
}
