package vn.mbm.phimp.me.wordpress;

import org.wordpress.android.fluxc.action.SiteAction;
import org.wordpress.android.fluxc.annotations.action.Action;
import org.wordpress.android.fluxc.annotations.action.ActionBuilder;
import org.wordpress.android.fluxc.model.SiteModel;
import org.wordpress.android.fluxc.model.SitesModel;
import org.wordpress.android.fluxc.network.rest.wpcom.site.SiteRestClient.DeleteSiteResponsePayload;
import org.wordpress.android.fluxc.network.rest.wpcom.site.SiteRestClient.ExportSiteResponsePayload;
import org.wordpress.android.fluxc.network.rest.wpcom.site.SiteRestClient.IsWPComResponsePayload;
import org.wordpress.android.fluxc.network.rest.wpcom.site.SiteRestClient.NewSiteResponsePayload;
import org.wordpress.android.fluxc.store.SiteStore.FetchedPostFormatsPayload;
import org.wordpress.android.fluxc.store.SiteStore.NewSitePayload;
import org.wordpress.android.fluxc.store.SiteStore.RefreshSitesXMLRPCPayload;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public final class SiteActionBuilder extends ActionBuilder {
    public SiteActionBuilder() {
    }

    public static Action<SiteModel> newFetchSiteAction(SiteModel payload) {
        return new Action(SiteAction.FETCH_SITE, payload);
    }

    public static Action<Void> newFetchSitesAction() {
        return generateNoPayloadAction(SiteAction.FETCH_SITES);
    }

    public static Action<RefreshSitesXMLRPCPayload> newFetchSitesXmlRpcAction(RefreshSitesXMLRPCPayload payload) {
        return new Action(SiteAction.FETCH_SITES_XML_RPC, payload);
    }


    public static Action<SiteModel> newFetchPostFormatsAction(SiteModel payload) {
        return new Action(SiteAction.FETCH_POST_FORMATS, payload);
    }

    public static Action<SiteModel> newDeleteSiteAction(SiteModel payload) {
        return new Action(SiteAction.DELETE_SITE, payload);
    }

    public static Action<SiteModel> newExportSiteAction(SiteModel payload) {
        return new Action(SiteAction.EXPORT_SITE, payload);
    }

    public static Action<String> newIsWpcomUrlAction(String payload) {
        return new Action(SiteAction.IS_WPCOM_URL, payload);
    }

    public static Action<NewSiteResponsePayload> newCreatedNewSiteAction(NewSiteResponsePayload payload) {
        return new Action(SiteAction.CREATED_NEW_SITE, payload);
    }

    public static Action<FetchedPostFormatsPayload> newFetchedPostFormatsAction(FetchedPostFormatsPayload payload) {
        return new Action(SiteAction.FETCHED_POST_FORMATS, payload);
    }

    public static Action<DeleteSiteResponsePayload> newDeletedSiteAction(DeleteSiteResponsePayload payload) {
        return new Action(SiteAction.DELETED_SITE, payload);
    }

    public static Action<ExportSiteResponsePayload> newExportedSiteAction(ExportSiteResponsePayload payload) {
        return new Action(SiteAction.EXPORTED_SITE, payload);
    }

    public static Action<SiteModel> newUpdateSiteAction(SiteModel payload) {
        return new Action(SiteAction.UPDATE_SITE, payload);
    }

    public static Action<SitesModel> newUpdateSitesAction(SitesModel payload) {
        return new Action(SiteAction.UPDATE_SITES, payload);
    }

    public static Action<SiteModel> newRemoveSiteAction(SiteModel payload) {
        return new Action(SiteAction.REMOVE_SITE, payload);
    }

    public static Action<Void> newRemoveAllSitesAction() {
        return generateNoPayloadAction(SiteAction.REMOVE_ALL_SITES);
    }

    public static Action<Void> newRemoveWpcomAndJetpackSitesAction() {
        return generateNoPayloadAction(SiteAction.REMOVE_WPCOM_AND_JETPACK_SITES);
    }

    public static Action<SitesModel> newShowSitesAction(SitesModel payload) {
        return new Action(SiteAction.SHOW_SITES, payload);
    }

    public static Action<SitesModel> newHideSitesAction(SitesModel payload) {
        return new Action(SiteAction.HIDE_SITES, payload);
    }

    public static Action<IsWPComResponsePayload> newCheckedIsWpcomUrlAction(IsWPComResponsePayload payload) {
        return new Action(SiteAction.CHECKED_IS_WPCOM_URL, payload);
    }
}
