/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.telecomm;

import android.content.ComponentName;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Represents a distinct account, line of service or call placement method that
 * the system can use to place phone calls.
 */
public class PhoneAccount implements Parcelable {


    /**
     * Flag indicating that this {@code PhoneAccount} can act as a call manager for traditional
     * SIM-based telephony calls. The {@link ConnectionService} associated with this phone-account
     * will be allowed to manage SIM-based phone calls including using its own proprietary
     * phone-call implementation (like VoIP calling) to make calls instead of the telephony stack.
     * When a user opts to place a call using the SIM-based telephony stack, the connection-service
     * associated with this phone-account will be attempted first if the user has explicitly
     * selected it to be used as the default call-manager.
     * <p>
     * See {@link #getCapabilities}
     */
    public static final int CAPABILITY_SIM_CALL_MANAGER = 0x1;

    /**
     * Flag indicating that this {@code PhoneAccount} can make phone calls in place of traditional
     * SIM-based telephony calls. This account will be treated as a distinct method for placing
     * calls alongside the traditional SIM-based telephony stack. This flag is distinct from
     * {@link #CAPABILITY_SIM_CALL_MANAGER} in that it is not allowed to manage calls from or use
     * the built-in telephony stack to place its calls.
     * <p>
     * See {@link #getCapabilities}
     */
    public static final int CAPABILITY_CALL_PROVIDER = 0x2;

    /**
     * Flag indicating that this {@code PhoneAccount} represents a built-in PSTN SIM subscription.
     * <p>
     * Only the android framework can set this capability on a phone-account.
     */
    public static final int CAPABILITY_SIM_SUBSCRIPTION = 0x4;

    private ComponentName mComponentName;
    private String mId;
    private Uri mHandle;
    private int mCapabilities;

    public PhoneAccount(
            ComponentName componentName,
            String id,
            Uri handle,
            int capabilities) {
        mComponentName = componentName;
        mId = id;
        mHandle = handle;
        mCapabilities = capabilities;
    }

    /**
     * The {@code ComponentName} of the {@link android.telecomm.ConnectionService} which is
     * responsible for making phone calls using this {@code PhoneAccount}.
     *
     * @return A suitable {@code ComponentName}.
     */
    public ComponentName getComponentName() {
        return mComponentName;
    }

    /**
     * A unique identifier for this {@code PhoneAccount}, generated by and meaningful to the
     * {@link android.telecomm.ConnectionService} that created it.
     *
     * @return A unique identifier for this {@code PhoneAccount}.
     */
    public String getId() {
        return mId;
    }

    /**
     * The handle (e.g., a phone number) associated with this {@code PhoneAccount}. This represents
     * the destination from which outgoing calls using this {@code PhoneAccount} will appear to
     * come, if applicable, and the destination to which incoming calls using this
     * {@code PhoneAccount} may be addressed.
     *
     * @return A handle expressed as a {@code Uri}, for example, a phone number.
     */
    public Uri getHandle() {
        return mHandle;
    }

    /**
     * The capabilities of this {@code PhoneAccount}.
     *
     * @return A bit field of flags describing this {@code PhoneAccount}'s capabilities.
     */
    public int getCapabilities() {
        return mCapabilities;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mComponentName) + Objects.hashCode(mId) +
                Objects.hashCode(mHandle) + mCapabilities;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(mComponentName)
                    .append(", ")
                    .append(mId)
                    .append(", ")
                    .append(Log.pii(mHandle))
                    .append(", ")
                    .append(String.valueOf(mCapabilities))
                    .toString();
    }

    /**
     * TODO: Change this to just be equals() and use Set<> in Telecomm code instead of Lists.
     * @hide
     */
    public boolean equalsComponentAndId(PhoneAccount other) {
        return other != null &&
                Objects.equals(other.getComponentName(), getComponentName()) &&
                Objects.equals(other.getId(), getId());
    }

    //
    // Parcelable implementation.
    //

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(mComponentName, flags);
        out.writeString(mId);
        out.writeString(mHandle != null ? mHandle.toString() : "");
        out.writeInt(mCapabilities);
    }

    public static final Creator<PhoneAccount> CREATOR = new Creator<PhoneAccount>() {
        @Override
        public PhoneAccount createFromParcel(Parcel in) {
            return new PhoneAccount(in);
        }

        @Override
        public PhoneAccount[] newArray(int size) {
            return new PhoneAccount[size];
        }
    };

    private PhoneAccount(Parcel in) {
        mComponentName = in.readParcelable(getClass().getClassLoader());
        mId = in.readString();
        String uriString = in.readString();
        mHandle = uriString.length() > 0 ? Uri.parse(uriString) : null;
        mCapabilities = in.readInt();
    }
}
