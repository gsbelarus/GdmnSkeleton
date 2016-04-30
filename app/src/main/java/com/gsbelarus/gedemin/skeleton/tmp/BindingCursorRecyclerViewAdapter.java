package com.gsbelarus.gedemin.skeleton.tmp;

//public class BindingCursorRecyclerViewAdapter extends BasicCursorRecyclerViewAdapter {

//    protected LayoutInflater layoutInflater;
//    private final Context appContext;
////    private final AttachedActivity attachedActivity;
//
//    @LayoutRes
//    protected int getLayoutId() {
//        return R.layout.recycler_item;
//    }
//
//
//    public BindingCursorRecyclerViewAdapter(@Nullable Cursor dataCursor, @NonNull Context appContext) {
//        super(dataCursor);
//
//        this.appContext = appContext;
//    }
//
//    @Override
//    public BaseCursorItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (layoutInflater == null) {
//            layoutInflater = LayoutInflater.from(parent.getContext());
//        }
//        final ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), parent, false);
//
//        return new BindingItemViewHolder(binding);
//    }
//
//    public Context getAppContext() {
//        return appContext;
//    }
//
//
//    public static class BindingItemViewHolder extends BaseCursorItemViewHolder {
//
//        protected final ViewDataBinding binding;
//
//        public BindingItemViewHolder(final ViewDataBinding binding) {
//            super(binding.getRoot());
//
//            this.binding = binding;
//        }
//
//        @Override
//        public void bind(Cursor cursor) {
//            //TODO
//            // viewModel.setCursor(cursor);
//            //binding.executePendingBindings();
//        }
//    }
//}
