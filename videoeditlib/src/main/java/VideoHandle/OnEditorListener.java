package VideoHandle;

/**
 * UI Command execute completed/ error lister
 */
public interface OnEditorListener {
	void onSuccess();

	void onFailure();

	void onProgress(float progress);
}
