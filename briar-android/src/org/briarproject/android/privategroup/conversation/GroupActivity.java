package org.briarproject.android.privategroup.conversation;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.briarproject.R;
import org.briarproject.android.ActivityComponent;
import org.briarproject.android.controller.handler.UiResultExceptionHandler;
import org.briarproject.android.threaded.ThreadListActivity;
import org.briarproject.android.threaded.ThreadListController;
import org.briarproject.api.db.DbException;
import org.briarproject.api.privategroup.GroupMessageHeader;
import org.briarproject.api.privategroup.PrivateGroup;

import javax.inject.Inject;

import static org.briarproject.api.privategroup.PrivateGroupConstants.MAX_GROUP_POST_BODY_LENGTH;

public class GroupActivity extends
		ThreadListActivity<PrivateGroup, GroupMessageItem, GroupMessageHeader>
		implements OnClickListener {

	@Inject
	GroupController controller;

	private boolean isCreator, isDissolved = false;
	private MenuItem writeMenuItem, leaveMenuItem, dissolveMenuItem;

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	protected ThreadListController<PrivateGroup, GroupMessageItem, GroupMessageHeader> getController() {
		return controller;
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);

		Intent i = getIntent();
		String groupName = i.getStringExtra(GROUP_NAME);
		if (groupName != null) setTitle(groupName);
		loadNamedGroup();

		setGroupEnabled(false);
	}

	@Override
	@LayoutRes
	protected int getLayout() {
		return R.layout.activity_forum;
	}

	@Override
	protected GroupMessageAdapter createAdapter(
			LinearLayoutManager layoutManager) {
		return new GroupMessageAdapter(this, layoutManager);
	}

	@Override
	protected void loadItems() {
		controller.isDissolved(
				new UiResultExceptionHandler<Boolean, DbException>(this) {
					@Override
					public void onResultUi(Boolean isDissolved) {
						setGroupEnabled(!isDissolved);
						GroupActivity.super.loadItems();
					}

					@Override
					public void onExceptionUi(DbException exception) {
						// TODO proper error handling
						finish();
					}
				});
	}

	@Override
	protected void onNamedGroupLoaded(PrivateGroup group) {
		setTitle(group.getName());
		// Created by
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setSubtitle(getString(R.string.groups_created_by,
					group.getAuthor().getName()));
		}
		controller.isCreator(group,
				new UiResultExceptionHandler<Boolean, DbException>(this) {
					@Override
					public void onResultUi(Boolean isCreator) {
						GroupActivity.this.isCreator = isCreator;
						showMenuItems();
					}

					@Override
					public void onExceptionUi(DbException exception) {
						// TODO proper error handling
						finish();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.group_actions, menu);

		writeMenuItem = menu.findItem(R.id.action_group_compose_message);
		leaveMenuItem = menu.findItem(R.id.action_group_leave);
		dissolveMenuItem = menu.findItem(R.id.action_group_dissolve);
		showMenuItems();

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_group_compose_message:
				showTextInput(null);
				return true;
			case R.id.action_group_leave:
				showLeaveGroupDialog();
				return true;
			case R.id.action_group_dissolve:
				showDissolveGroupDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected int getMaxBodyLength() {
		return MAX_GROUP_POST_BODY_LENGTH;
	}

	@Override
	@StringRes
	protected int getItemPostedString() {
		return R.string.groups_message_sent;
	}

	@Override
	@StringRes
	protected int getItemReceivedString() {
		return R.string.groups_message_received;
	}

	@Override
	public void onReplyClick(GroupMessageItem item) {
		if (!isDissolved) super.onReplyClick(item);
	}

	private void setGroupEnabled(boolean enabled) {
		isDissolved = !enabled;
		if (writeMenuItem != null) writeMenuItem.setVisible(enabled);
		textInput.setSendButtonEnabled(enabled);
		list.getRecyclerView().setAlpha(enabled ? 1f : 0.5f);
	}

	private void showMenuItems() {
		if (leaveMenuItem == null || dissolveMenuItem == null) return;
		if (isCreator) {
			leaveMenuItem.setVisible(false);
			dissolveMenuItem.setVisible(true);
		} else {
			leaveMenuItem.setVisible(true);
			dissolveMenuItem.setVisible(false);
		}
		writeMenuItem.setVisible(!isDissolved);
	}

	private void showLeaveGroupDialog() {
		AlertDialog.Builder builder =
				new AlertDialog.Builder(this, R.style.BriarDialogTheme);
		builder.setTitle(getString(R.string.groups_leave_dialog_title));
		builder.setMessage(getString(R.string.groups_leave_dialog_message));
		builder.setNegativeButton(R.string.dialog_button_leave, this);
		builder.setPositiveButton(R.string.cancel, null);
		builder.show();
	}

	private void showDissolveGroupDialog() {
		AlertDialog.Builder builder =
				new AlertDialog.Builder(this, R.style.BriarDialogTheme);
		builder.setTitle(getString(R.string.groups_dissolve_dialog_title));
		builder.setMessage(getString(R.string.groups_dissolve_dialog_message));
		builder.setNegativeButton(R.string.groups_dissolve_button, this);
		builder.setPositiveButton(R.string.cancel, null);
		builder.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		controller.deleteNamedGroup(
				new UiResultExceptionHandler<Void, DbException>(this) {
					@Override
					public void onResultUi(Void v) {
						// The activity is going to be destroyed by the
						// GroupRemovedEvent being fired
					}

					@Override
					public void onExceptionUi(DbException exception) {
						// TODO proper error handling
						finish();
					}
				});
	}

}