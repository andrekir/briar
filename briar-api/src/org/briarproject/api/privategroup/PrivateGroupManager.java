package org.briarproject.api.privategroup;

import org.briarproject.api.clients.MessageTracker;
import org.briarproject.api.db.DbException;
import org.briarproject.api.db.Transaction;
import org.briarproject.api.sync.ClientId;
import org.briarproject.api.sync.GroupId;
import org.briarproject.api.sync.MessageId;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PrivateGroupManager extends MessageTracker {

	/** Returns the unique ID of the private group client. */
	@NotNull
	ClientId getClientId();

	/** Stores (and sends) a local group message. */
	void addLocalMessage(GroupMessage p) throws DbException;

	/** Returns the private group with the given ID. */
	@NotNull
	PrivateGroup getPrivateGroup(GroupId g) throws DbException;

	/**
	 * Returns the private group with the given ID within the given transaction.
	 */
	@NotNull
	PrivateGroup getPrivateGroup(Transaction txn, GroupId g) throws DbException;

	/** Returns all private groups the user is a member of. */
	@NotNull
	Collection<PrivateGroup> getPrivateGroups() throws DbException;

	/** Returns the body of the group message with the given ID. */
	@NotNull
	String getMessageBody(MessageId m) throws DbException;

	/** Returns the headers of all group messages in the given group. */
	@NotNull
	Collection<GroupMessageHeader> getHeaders(GroupId g) throws DbException;

}
