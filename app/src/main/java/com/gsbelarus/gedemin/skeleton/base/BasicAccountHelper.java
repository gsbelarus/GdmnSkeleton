package com.gsbelarus.gedemin.skeleton.base;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gsbelarus.gedemin.skeleton.R;
import com.gsbelarus.gedemin.skeleton.base.view.adapter.BaseSimpleAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicAccountHelper {

    public static final String KEY_SELECTED_ACCOUNT = "selected_account";

    private Context context;
    private Account oldSelectedAccount;
    private Account[] oldAccounts;

    private OnChangedListener onChangedListener;
    private OnDeletedListener onDeletedListener;

    private OnAccountsUpdateListener onAccountsUpdateListener = new OnAccountsUpdateListener() {
        @Override
        public void onAccountsUpdated(Account[] accounts) {
            if (oldAccounts != null) {
                List<Account> newAccounts = Arrays.asList(accounts);
                for (Account account : oldAccounts) {
                    if (!newAccounts.contains(account)) {
                        notifyOnDeletedListener(account);
                    }
                }
            }
            oldAccounts = accounts;

            if (getSelectedAccount(context) == null) {
                notifyOnChangedListener(oldSelectedAccount, null);
            }
        }
    };

    private LifeCycleDelegate lifeCycleDelegate = new LifeCycleDelegate() {
        @Override
        public void onCreate() {
            AccountManager.get(context).addOnAccountsUpdatedListener(onAccountsUpdateListener, null, true);
        }

        @Override
        public void onDestroy() {
            AccountManager.get(context).removeOnAccountsUpdatedListener(onAccountsUpdateListener);
        }
    };

    private AccountChangeManager accountChangeManager = new AccountChangeManager() {

        private AlertDialog chooseDialog;

        @Override
        public void onCreate() {
            lifeCycleDelegate.onCreate();
            oldSelectedAccount = getSelectedAccount(context);
        }

        @Override
        public void onDestroy() {
            lifeCycleDelegate.onDestroy();
            cancelChooseAccount();
        }

        @Override
        public void onResume() {
            Account selectedAccount = getSelectedAccount(context);
            if (selectedAccount != null && !selectedAccount.equals(oldSelectedAccount)) {
                notifyOnChangedListener(oldSelectedAccount, selectedAccount);
            }
        }

        @Override
        public void setSelectedAccount(Account selectedAccount) {
            Account oldAccount = getSelectedAccount(context);
            if (BasicAccountHelper.setSelectedAccount(context, selectedAccount)) {
                oldSelectedAccount = selectedAccount;
                notifyOnChangedListener(oldAccount, selectedAccount);
            }
        }

        @Override
        public void chooseAccount(final Activity activity, final String accountType) {
            final AccountManager accountManager = AccountManager.get(context);
            final Account[] accounts = accountManager.getAccounts();
            final List<String> list = new ArrayList<>();
            Account selectedAccount = getSelectedAccount(context);
            int checkedItem = -1;
            for (int i = 0; i < accounts.length; i++) {
                if (selectedAccount != null && selectedAccount.equals(accounts[i])) {
                    checkedItem = i;
                }
                list.add(accounts[i].name);
            }

            cancelChooseAccount();
            chooseDialog = new AlertDialog.Builder(activity)
                    .setTitle("Choose account")
//                    .setSingleChoiceItems(list.toArray(new String[list.size()]), checkedItem, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            accountChangeManager.setSelectedAccount(accounts[which]);
//                            dialog.cancel();
//                        }
//                    })
                    .setNegativeButton("Cancel", null)
                    .setNeutralButton("New", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AccountManager.get(context).addAccount(accountType, accountType, null, null,
                                    activity, new AccountManagerCallback<Bundle>() {
                                        @Override
                                        public void run(AccountManagerFuture<Bundle> future) {
                                            try {
                                                Bundle bundle = future.getResult();
                                                Account account = new Account(
                                                        bundle.getString(AccountManager.KEY_ACCOUNT_NAME),
                                                        bundle.getString(AccountManager.KEY_ACCOUNT_TYPE));
                                                setSelectedAccount(account);
                                            } catch (OperationCanceledException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            } catch (AuthenticatorException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, null);
                        }
                    })
                    .create();

            RecyclerView recyclerView = new RecyclerView(activity.getBaseContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(activity.getBaseContext()));
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            recyclerView.setPadding(0, dpToPixel(8, context), 0, dpToPixel(8, context));

            final Adapter adapter = new Adapter();
            adapter.setCheckedPosition(checkedItem);
            adapter.setItems(list);
            adapter.setOnItemClickListener(new BaseSimpleAdapter.OnItemClickListener<String, Adapter.ItemVH>() {
                @Override
                public void onClick(View view, String item, Adapter.ItemVH itemVH) {
                    accountChangeManager.setSelectedAccount(accounts[itemVH.getAdapterPosition()]);
                    adapter.setCheckedPosition(itemVH.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                    chooseDialog.cancel();
                }
            });
            adapter.setOnRemovedClickListener(new BaseSimpleAdapter.OnItemClickListener<String, Adapter.ItemVH>() {
                @Override
                public void onClick(View view, String item, Adapter.ItemVH itemVH) {
                    AccountManager.get(context).removeAccount(accounts[itemVH.getAdapterPosition()], null, null);
                    chooseDialog.cancel();
                }
            });
            recyclerView.setAdapter(adapter);

            chooseDialog.setView(recyclerView);
            chooseDialog.show();
        }

        @Override
        public void cancelChooseAccount() {
            if (chooseDialog != null && chooseDialog.isShowing()) {
                chooseDialog.cancel();
            }
        }

        private int dpToPixel(float dp, Context context) {
            return (int) (dp * ((float) context.getResources().
                    getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    };

    public BasicAccountHelper(Context context) {
        this.context = context;
    }

    public static Account getSelectedAccount(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        for (Account account : accountManager.getAccounts()) {
            if (Boolean.valueOf(accountManager.getUserData(account, KEY_SELECTED_ACCOUNT))) {
                return account;
            }
        }
        return null;
    }

    public static boolean setSelectedAccount(Context context, Account selectedAccount) {
        Account oldAccount = getSelectedAccount(context);
        if (oldAccount == null || !oldAccount.equals(selectedAccount)) {
            AccountManager accountManager = AccountManager.get(context);
            for (Account account : accountManager.getAccounts()) {
                accountManager.setUserData(account, KEY_SELECTED_ACCOUNT, String.valueOf(account.equals(selectedAccount)));
            }
            return true;
        }
        return false;
    }

    private void notifyOnChangedListener(Account oldAccount, Account newAccount) {
        if (onChangedListener != null) {
            Log.d(this.getClass().getSimpleName(), "Account changed from \"" +
                    (oldAccount == null ? "null" : oldAccount.name) + "\" to \"" +
                    (newAccount == null ? "null" : newAccount.name) + "\"");
            onChangedListener.onChanged(oldAccount, newAccount);
        }
    }

    private void notifyOnDeletedListener(Account account) {
        if (onDeletedListener != null) {
            Log.d(this.getClass().getSimpleName(), "Account \"" + account.name + "\" deleted");
            onDeletedListener.onDeleted(account);
        }
    }

    public OnChangedListener getOnChangedListener() {
        return onChangedListener;
    }

    /**
     * Для корректной работы listener-а необходимо использовать {@link AccountChangeManager} при изменении
     * пользователя и состояния activity/fragment/service
     */
    public AccountChangeManager setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
        return accountChangeManager;
    }

    public OnDeletedListener getOnDeletedListener() {
        return onDeletedListener;
    }

    /**
     * Для работы listener-а необходимо использовать {@link LifeCycleDelegate} для отслеживания состояния
     * activity/fragment/service
     */
    public LifeCycleDelegate setOnDeletedListener(OnDeletedListener onDeletedListener) {
        this.onDeletedListener = onDeletedListener;
        return lifeCycleDelegate;
    }

    public interface OnChangedListener {
        void onChanged(Account oldAccount, Account newAccount);
    }

    public interface OnDeletedListener {
        void onDeleted(Account account);
    }

    public interface LifeCycleDelegate {
        void onCreate();

        void onDestroy();
    }

    public interface AccountChangeManager extends LifeCycleDelegate {
        void onResume();

        void setSelectedAccount(Account selectedAccount);

        void chooseAccount(Activity activity, String accountType);

        void cancelChooseAccount();
    }

    private class Adapter extends BaseSimpleAdapter<String, Adapter.ItemVH> {

        private OnItemClickListener<String, ItemVH> onRemovedClickListener;
        private int checkedPosition = -1;

        @Override
        public ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemVH(inflateView(R.layout.basic_account_item, parent));
        }

        @Override
        public void onBindViewHolder(ItemVH holder, int position) {
            String name = getItem(position);
            holder.accountTv.setText(name);
            holder.checkRb.setChecked(checkedPosition == position);
        }

        public int getCheckedPosition() {
            return checkedPosition;
        }

        public void setCheckedPosition(int checkedPosition) {
            this.checkedPosition = checkedPosition;
        }

        public OnItemClickListener<String, ItemVH> getOnRemovedClickListener() {
            return onRemovedClickListener;
        }

        public void setOnRemovedClickListener(OnItemClickListener<String, ItemVH> onRemovedClickListener) {
            this.onRemovedClickListener = onRemovedClickListener;
        }

        public class ItemVH extends BaseSimpleAdapter.ItemViewHolder {

            private TextView accountTv;
            private RadioButton checkRb;
            private ImageView removeIv;

            public ItemVH(final View itemView) {
                super(itemView);

                accountTv = (TextView) itemView.findViewById(R.id.basic_account_name);
                checkRb = (RadioButton) itemView.findViewById(R.id.basic_account_check);
                removeIv = (ImageView) itemView.findViewById(R.id.basic_account_remove);
                removeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getAdapterPosition() != -1) {
                            if (onRemovedClickListener != null) {
                                onRemovedClickListener.onClick(itemView, getItem(getAdapterPosition()), ItemVH.this);
                            }
                        }
                    }
                });
            }
        }
    }
}
