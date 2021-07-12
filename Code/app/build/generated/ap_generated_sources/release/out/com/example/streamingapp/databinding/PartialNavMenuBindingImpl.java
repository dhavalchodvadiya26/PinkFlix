package com.example.streamingapp.databinding;
import com.example.streamingapp.R;
import com.example.streamingapp.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class PartialNavMenuBindingImpl extends PartialNavMenuBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.ll_home, 6);
        sViewsWithIds.put(R.id.exp_menu, 7);
        sViewsWithIds.put(R.id.ll_setting, 8);
    }
    // views
    @NonNull
    private final androidx.core.widget.NestedScrollView mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public PartialNavMenuBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 9, sIncludes, sViewsWithIds));
    }
    private PartialNavMenuBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (com.example.view.NonScrollExpandableListView) bindings[7]
            , (android.widget.LinearLayout) bindings[1]
            , (android.widget.LinearLayout) bindings[6]
            , (android.widget.LinearLayout) bindings[4]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.LinearLayout) bindings[8]
            , (android.widget.LinearLayout) bindings[2]
            , (android.widget.LinearLayout) bindings[3]
            );
        this.llDashboard.setTag(null);
        this.llLogin.setTag(null);
        this.llLogout.setTag(null);
        this.llTransaction.setTag(null);
        this.llWatchlist.setTag(null);
        this.mboundView0 = (androidx.core.widget.NestedScrollView) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.isLogin == variableId) {
            setIsLogin((java.lang.Boolean) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setIsLogin(@Nullable java.lang.Boolean IsLogin) {
        this.mIsLogin = IsLogin;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.isLogin);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        int isLoginViewGONEViewVISIBLE = 0;
        java.lang.Boolean isLogin = mIsLogin;
        int isLoginViewVISIBLEViewGONE = 0;
        boolean androidxDatabindingViewDataBindingSafeUnboxIsLogin = false;

        if ((dirtyFlags & 0x3L) != 0) {



                // read androidx.databinding.ViewDataBinding.safeUnbox(isLogin)
                androidxDatabindingViewDataBindingSafeUnboxIsLogin = androidx.databinding.ViewDataBinding.safeUnbox(isLogin);
            if((dirtyFlags & 0x3L) != 0) {
                if(androidxDatabindingViewDataBindingSafeUnboxIsLogin) {
                        dirtyFlags |= 0x8L;
                        dirtyFlags |= 0x20L;
                }
                else {
                        dirtyFlags |= 0x4L;
                        dirtyFlags |= 0x10L;
                }
            }


                // read androidx.databinding.ViewDataBinding.safeUnbox(isLogin) ? View.GONE : View.VISIBLE
                isLoginViewGONEViewVISIBLE = ((androidxDatabindingViewDataBindingSafeUnboxIsLogin) ? (android.view.View.GONE) : (android.view.View.VISIBLE));
                // read androidx.databinding.ViewDataBinding.safeUnbox(isLogin) ? View.VISIBLE : View.GONE
                isLoginViewVISIBLEViewGONE = ((androidxDatabindingViewDataBindingSafeUnboxIsLogin) ? (android.view.View.VISIBLE) : (android.view.View.GONE));
        }
        // batch finished
        if ((dirtyFlags & 0x3L) != 0) {
            // api target 1

            this.llDashboard.setVisibility(isLoginViewVISIBLEViewGONE);
            this.llLogin.setVisibility(isLoginViewGONEViewVISIBLE);
            this.llLogout.setVisibility(isLoginViewVISIBLEViewGONE);
            this.llTransaction.setVisibility(isLoginViewVISIBLEViewGONE);
            this.llWatchlist.setVisibility(isLoginViewVISIBLEViewGONE);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): isLogin
        flag 1 (0x2L): null
        flag 2 (0x3L): androidx.databinding.ViewDataBinding.safeUnbox(isLogin) ? View.GONE : View.VISIBLE
        flag 3 (0x4L): androidx.databinding.ViewDataBinding.safeUnbox(isLogin) ? View.GONE : View.VISIBLE
        flag 4 (0x5L): androidx.databinding.ViewDataBinding.safeUnbox(isLogin) ? View.VISIBLE : View.GONE
        flag 5 (0x6L): androidx.databinding.ViewDataBinding.safeUnbox(isLogin) ? View.VISIBLE : View.GONE
    flag mapping end*/
    //end
}