package data.interfaces;

import data.Conversation;

/**
 * Anything you can talk to, that has a {@link Conversation}.
 * 
 * @author Satia
 */
public interface HasConversation extends Identifiable {

	/**
	 * Gets the conversation. Can be {@code null}.
	 * 
	 * @return the conversation.
	 */
	public Conversation getConversation();

	/**
	 * Sets the conversation. {@code null} means no conversation.
	 * 
	 * @param conversation
	 *            the conversation to set.
	 */
	public void setConversation(Conversation conversation);

	/**
	 * @return if talking is enabled.
	 */
	public boolean isTalkingEnabled();

	/**
	 * @return the text displayed when trying to talk to, but this is disabled.
	 */
	public String getTalkingToForbiddenText();

	/**
	 * Sets the forbidden text for talking. If {@code null} passed, the default
	 * text will be used. TODO default
	 * 
	 * @param text
	 *            the forbiddenText to set
	 */
	public void setTalkingToForbiddenText(String text);

	/**
	 * Triggers all additional actions of the conversation and, if enabled, the
	 * conversation will be started.
	 */
	public void talkTo();
}
