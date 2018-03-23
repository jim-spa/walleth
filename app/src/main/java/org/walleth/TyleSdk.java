package org.walleth;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.salomonbrys.kodein.Kodein;
import com.github.salomonbrys.kodein.KodeinAware;

import org.jetbrains.annotations.NotNull;
import org.kethereum.model.Address;
import org.kethereum.model.ChainDefinition;
import org.walleth.activities.SdkMainActivity;
import org.walleth.data.balances.Balance;
import org.walleth.data.networks.CurrentAddressProvider;
import org.walleth.data.networks.NetworkDefinitionProvider;
import org.walleth.data.tokens.CurrentTokenProvider;
import org.walleth.data.transactions.TransactionEntity;
import org.walleth.ui.ValueView;

import java.util.List;

import timber.log.Timber;

import static java.math.BigDecimal.ZERO;
import static org.threeten.bp.zone.ZoneRulesProvider.refresh;

/**
 * Created by sirisdev on 3/4/18.
 */

public class TyleSdk implements Application.ActivityLifecycleCallbacks, KodeinAware {

    private static App sdkApp;
    private static NetworkDefinitionProvider networkDefinitionProvider;
    private static CurrentAddressProvider currentAddressProvider;
    private static CurrentTokenProvider currentTokenProvider;
    private static Activity mainActivity;
    private static int mainIcon;
    private static String mainDescr;

    /**
     * TODO
     * Move this to the SDK so that it installs with it
     */
    private LiveData<List<TransactionEntity>> incomingTransactionsForAddress = null;
    private LiveData<List<TransactionEntity>> outgoingTransactionsForAddress = null;
    private LiveData<Balance> balanceLiveData = null;

    public void setCurrentBalanceObserver(LifecycleOwner owner, View v) {
        balanceLiveData = sdkApp.getAppDatabase().getBalances().getBalanceLive(currentAddressProvider.getCurrent(),
                currentTokenProvider.getCurrentToken().getAddress(), networkDefinitionProvider.getCurrent().getChain());
        balanceLiveData.observe(owner, balance ->
                ((ValueView) v).setValue(balance != null ? balance.getBalance() : ZERO.toBigInteger(),
                        currentTokenProvider.getCurrentToken()));
    }

    private void transactionEntitiesRefresh(List<TransactionEntity> transactionEntities) {
        if (transactionEntities != null) {
            refresh();
        }
    }

    public void installTransactionObservers(LifecycleOwner owner) {
        if (currentAddressProvider.getValue() == null) {
            Address currentAddress = currentAddressProvider.getCurrent();
            ChainDefinition currentChain = networkDefinitionProvider.getCurrent().getChain();
            incomingTransactionsForAddress = sdkApp.getAppDatabase().getTransactions().getIncomingTransactionsForAddressOnChainOrdered(currentAddress, currentChain);
            outgoingTransactionsForAddress = sdkApp.getAppDatabase().getTransactions().getOutgoingTransactionsForAddressOnChainOrdered(currentAddress, currentChain);

            incomingTransactionsForAddress.observe(owner, this::transactionEntitiesRefresh);
            outgoingTransactionsForAddress.observe(owner, this::transactionEntitiesRefresh);
        }
    }

    public void init(App sdkApp, Activity mainActivity, int mainIcon, String mainDescr) {
        networkDefinitionProvider = sdkApp.getNetworkDefinitionProvider();
        currentAddressProvider = sdkApp.getCurrentAddressProvider();
        currentTokenProvider = sdkApp.getCurrentTokenProvider();
        Log.d("OpenSudoku", "onCreate");
        Timber.d("no Activity in onCreate");
        TyleSdk.sdkApp = sdkApp;
        TyleSdk.mainActivity = mainActivity;
        TyleSdk.mainIcon = mainIcon;
        TyleSdk.mainDescr = mainDescr;
        sdkApp.registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d("OpenSudoku", "onActivityCreated");
        Timber.d("Activity[%s] created", activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("OpenSudoku", "onActivityStarted");
        Timber.d("Activity[%s] started", activity.getClass().getSimpleName());
        if (activity instanceof SdkMainActivity) {
            ((SdkMainActivity) activity).setGameInfo(TyleSdk.mainActivity, TyleSdk.mainIcon, TyleSdk.mainDescr);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("OpenSudoku", "onActivityResumed");
        Timber.d("Activity[%s] started", activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @NotNull
    @Override
    public Kodein getKodein() {
        return sdkApp.getKodein();
    }
}
