package com.synway.flinkodps.odpsupload.kafka;

import java.io.Serializable;

/**
 * Create by tfy on 2019/12/3 16:07
 **/


public final class RecordProbuf implements Serializable {
    private static final long serialVersionUID = -5178682200897520661L;

    private RecordProbuf() {}
    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
    }
    public interface RecordSetOrBuilder
            extends com.google.protobuf.MessageOrBuilder {

        // required .com.synway.flinkodps.odpsupload.kafka.Attribute attri = 1;
        boolean hasAttri();
        com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute getAttri();
        com.synway.flinkodps.odpsupload.kafka.RecordProbuf.AttributeOrBuilder getAttriOrBuilder();

        // repeated bytes record = 2;
        java.util.List<com.google.protobuf.ByteString> getRecordList();
        int getRecordCount();
        com.google.protobuf.ByteString getRecord(int index);
    }
    public static final class RecordSet extends
            com.google.protobuf.GeneratedMessage
            implements RecordSetOrBuilder {
        // Use RecordSet.newBuilder() to construct.
        private RecordSet(Builder builder) {
            super(builder);
        }
        private RecordSet(boolean noInit) {}

        private static final RecordSet defaultInstance;
        public static RecordSet getDefaultInstance() {
            return defaultInstance;
        }

        public RecordSet getDefaultInstanceForType() {
            return defaultInstance;
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_RecordSet_descriptor;
        }

        protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_RecordSet_fieldAccessorTable;
        }

        private int bitField0_;
        // required .com.synway.flinkodps.odpsupload.kafka.Attribute attri = 1;
        public static final int ATTRI_FIELD_NUMBER = 1;
        private com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute attri_;
        public boolean hasAttri() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }
        public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute getAttri() {
            return attri_;
        }
        public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.AttributeOrBuilder getAttriOrBuilder() {
            return attri_;
        }

        // repeated bytes record = 2;
        public static final int RECORD_FIELD_NUMBER = 2;
        private java.util.List<com.google.protobuf.ByteString> record_;
        public java.util.List<com.google.protobuf.ByteString>
        getRecordList() {
            return record_;
        }
        public int getRecordCount() {
            return record_.size();
        }
        public com.google.protobuf.ByteString getRecord(int index) {
            return record_.get(index);
        }

        private void initFields() {
            attri_ = com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDefaultInstance();
            record_ = java.util.Collections.emptyList();;
        }
        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized != -1) return isInitialized == 1;

            if (!hasAttri()) {
                memoizedIsInitialized = 0;
                return false;
            }
            if (!getAttri().isInitialized()) {
                memoizedIsInitialized = 0;
                return false;
            }
            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            getSerializedSize();
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                output.writeMessage(1, attri_);
            }
            for (int i = 0; i < record_.size(); i++) {
                output.writeBytes(2, record_.get(i));
            }
            getUnknownFields().writeTo(output);
        }

        private int memoizedSerializedSize = -1;
        public int getSerializedSize() {
            int size = memoizedSerializedSize;
            if (size != -1) return size;

            size = 0;
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(1, attri_);
            }
            {
                int dataSize = 0;
                for (int i = 0; i < record_.size(); i++) {
                    dataSize += com.google.protobuf.CodedOutputStream
                            .computeBytesSizeNoTag(record_.get(i));
                }
                size += dataSize;
                size += 1 * getRecordList().size();
            }
            size += getUnknownFields().getSerializedSize();
            memoizedSerializedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @Override
        protected Object writeReplace()
                throws java.io.ObjectStreamException {
            return super.writeReplace();
        }

        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data, extensionRegistry)
                    .buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data, extensionRegistry)
                    .buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input, extensionRegistry)
                    .buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            Builder builder = newBuilder();
            if (builder.mergeDelimitedFrom(input)) {
                return builder.buildParsed();
            } else {
                return null;
            }
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            Builder builder = newBuilder();
            if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
                return builder.buildParsed();
            } else {
                return null;
            }
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input, extensionRegistry)
                    .buildParsed();
        }

        public static Builder newBuilder() { return Builder.create(); }
        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder(com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() { return newBuilder(this); }

        @Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        public static final class Builder extends
                com.google.protobuf.GeneratedMessage.Builder<Builder>
                implements com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSetOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_RecordSet_descriptor;
            }

            protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_RecordSet_fieldAccessorTable;
            }

            // Construct using com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
                    getAttriFieldBuilder();
                }
            }
            private static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                if (attriBuilder_ == null) {
                    attri_ = com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDefaultInstance();
                } else {
                    attriBuilder_.clear();
                }
                bitField0_ = (bitField0_ & ~0x00000001);
                record_ = java.util.Collections.emptyList();;
                bitField0_ = (bitField0_ & ~0x00000002);
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet.getDescriptor();
            }

            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet getDefaultInstanceForType() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet.getDefaultInstance();
            }

            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet build() {
                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            private com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet buildParsed()
                    throws com.google.protobuf.InvalidProtocolBufferException {
                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(
                            result).asInvalidProtocolBufferException();
                }
                return result;
            }

            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet buildPartial() {
                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet result = new com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                    to_bitField0_ |= 0x00000001;
                }
                if (attriBuilder_ == null) {
                    result.attri_ = attri_;
                } else {
                    result.attri_ = attriBuilder_.build();
                }
                if (((bitField0_ & 0x00000002) == 0x00000002)) {
                    record_ = java.util.Collections.unmodifiableList(record_);
                    bitField0_ = (bitField0_ & ~0x00000002);
                }
                result.record_ = record_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet) {
                    return mergeFrom((com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet other) {
                if (other == com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet.getDefaultInstance()) return this;
                if (other.hasAttri()) {
                    mergeAttri(other.getAttri());
                }
                if (!other.record_.isEmpty()) {
                    if (record_.isEmpty()) {
                        record_ = other.record_;
                        bitField0_ = (bitField0_ & ~0x00000002);
                    } else {
                        ensureRecordIsMutable();
                        record_.addAll(other.record_);
                    }
                    onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                if (!hasAttri()) {

                    return false;
                }
                if (!getAttri().isInitialized()) {

                    return false;
                }
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                        com.google.protobuf.UnknownFieldSet.newBuilder(
                                this.getUnknownFields());
                while (true) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            this.setUnknownFields(unknownFields.build());
                            onChanged();
                            return this;
                        default: {
                            if (!parseUnknownField(input, unknownFields,
                                    extensionRegistry, tag)) {
                                this.setUnknownFields(unknownFields.build());
                                onChanged();
                                return this;
                            }
                            break;
                        }
                        case 10: {
                            com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.Builder subBuilder = com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.newBuilder();
                            if (hasAttri()) {
                                subBuilder.mergeFrom(getAttri());
                            }
                            input.readMessage(subBuilder, extensionRegistry);
                            setAttri(subBuilder.buildPartial());
                            break;
                        }
                        case 18: {
                            ensureRecordIsMutable();
                            record_.add(input.readBytes());
                            break;
                        }
                    }
                }
            }

            private int bitField0_;

            // required .com.synway.flinkodps.odpsupload.kafka.Attribute attri = 1;
            private com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute attri_ = com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDefaultInstance();
            private com.google.protobuf.SingleFieldBuilder<
                    com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute, com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.Builder, com.synway.flinkodps.odpsupload.kafka.RecordProbuf.AttributeOrBuilder> attriBuilder_;
            public boolean hasAttri() {
                return ((bitField0_ & 0x00000001) == 0x00000001);
            }
            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute getAttri() {
                if (attriBuilder_ == null) {
                    return attri_;
                } else {
                    return attriBuilder_.getMessage();
                }
            }
            public Builder setAttri(com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute value) {
                if (attriBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    attri_ = value;
                    onChanged();
                } else {
                    attriBuilder_.setMessage(value);
                }
                bitField0_ |= 0x00000001;
                return this;
            }
            public Builder setAttri(
                    com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.Builder builderForValue) {
                if (attriBuilder_ == null) {
                    attri_ = builderForValue.build();
                    onChanged();
                } else {
                    attriBuilder_.setMessage(builderForValue.build());
                }
                bitField0_ |= 0x00000001;
                return this;
            }
            public Builder mergeAttri(com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute value) {
                if (attriBuilder_ == null) {
                    if (((bitField0_ & 0x00000001) == 0x00000001) &&
                            attri_ != com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDefaultInstance()) {
                        attri_ =
                                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.newBuilder(attri_).mergeFrom(value).buildPartial();
                    } else {
                        attri_ = value;
                    }
                    onChanged();
                } else {
                    attriBuilder_.mergeFrom(value);
                }
                bitField0_ |= 0x00000001;
                return this;
            }
            public Builder clearAttri() {
                if (attriBuilder_ == null) {
                    attri_ = com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDefaultInstance();
                    onChanged();
                } else {
                    attriBuilder_.clear();
                }
                bitField0_ = (bitField0_ & ~0x00000001);
                return this;
            }
            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.Builder getAttriBuilder() {
                bitField0_ |= 0x00000001;
                onChanged();
                return getAttriFieldBuilder().getBuilder();
            }
            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.AttributeOrBuilder getAttriOrBuilder() {
                if (attriBuilder_ != null) {
                    return attriBuilder_.getMessageOrBuilder();
                } else {
                    return attri_;
                }
            }
            private com.google.protobuf.SingleFieldBuilder<
                    com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute, com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.Builder, com.synway.flinkodps.odpsupload.kafka.RecordProbuf.AttributeOrBuilder>
            getAttriFieldBuilder() {
                if (attriBuilder_ == null) {
                    attriBuilder_ = new com.google.protobuf.SingleFieldBuilder<
                            com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute, com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.Builder, com.synway.flinkodps.odpsupload.kafka.RecordProbuf.AttributeOrBuilder>(
                            attri_,
                            getParentForChildren(),
                            isClean());
                    attri_ = null;
                }
                return attriBuilder_;
            }

            // repeated bytes record = 2;
            private java.util.List<com.google.protobuf.ByteString> record_ = java.util.Collections.emptyList();;
            private void ensureRecordIsMutable() {
                if (!((bitField0_ & 0x00000002) == 0x00000002)) {
                    record_ = new java.util.ArrayList<com.google.protobuf.ByteString>(record_);
                    bitField0_ |= 0x00000002;
                }
            }
            public java.util.List<com.google.protobuf.ByteString>
            getRecordList() {
                return java.util.Collections.unmodifiableList(record_);
            }
            public int getRecordCount() {
                return record_.size();
            }
            public com.google.protobuf.ByteString getRecord(int index) {
                return record_.get(index);
            }
            public Builder setRecord(
                    int index, com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureRecordIsMutable();
                record_.set(index, value);
                onChanged();
                return this;
            }
            public Builder addRecord(com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                ensureRecordIsMutable();
                record_.add(value);
                onChanged();
                return this;
            }
            public Builder addAllRecord(
                    Iterable<? extends com.google.protobuf.ByteString> values) {
                ensureRecordIsMutable();
                super.addAll(values, record_);
                onChanged();
                return this;
            }
            public Builder clearRecord() {
                record_ = java.util.Collections.emptyList();;
                bitField0_ = (bitField0_ & ~0x00000002);
                onChanged();
                return this;
            }

            // @@protoc_insertion_point(builder_scope:com.synway.flinkodps.odpsupload.kafka.RecordSet)
        }

        static {
            defaultInstance = new RecordSet(true);
            defaultInstance.initFields();
        }

        // @@protoc_insertion_point(class_scope:com.synway.flinkodps.odpsupload.kafka.RecordSet)
    }

    public interface AttributeOrBuilder
            extends com.google.protobuf.MessageOrBuilder {

        // required string datasource = 1;
        boolean hasDatasource();
        String getDatasource();

        // required string datatype = 2;
        boolean hasDatatype();
        String getDatatype();

        // required uint32 count = 3;
        boolean hasCount();
        int getCount();

        // optional uint32 datadate = 4;
        boolean hasDatadate();
        int getDatadate();

        // optional uint32 createdate = 5;
        boolean hasCreatedate();
        int getCreatedate();

        // optional string datatypeEx = 6;
        boolean hasDatatypeEx();
        String getDatatypeEx();
    }
    public static final class Attribute extends
            com.google.protobuf.GeneratedMessage
            implements AttributeOrBuilder {
        // Use Attribute.newBuilder() to construct.
        private Attribute(Builder builder) {
            super(builder);
        }
        private Attribute(boolean noInit) {}

        private static final Attribute defaultInstance;
        public static Attribute getDefaultInstance() {
            return defaultInstance;
        }

        public Attribute getDefaultInstanceForType() {
            return defaultInstance;
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_Attribute_descriptor;
        }

        protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_Attribute_fieldAccessorTable;
        }

        private int bitField0_;
        // required string datasource = 1;
        public static final int DATASOURCE_FIELD_NUMBER = 1;
        private Object datasource_;
        public boolean hasDatasource() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }
        public String getDatasource() {
            Object ref = datasource_;
            if (ref instanceof String) {
                return (String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                String s = bs.toStringUtf8();
                if (com.google.protobuf.Internal.isValidUtf8(bs)) {
                    datasource_ = s;
                }
                return s;
            }
        }
        private com.google.protobuf.ByteString getDatasourceBytes() {
            Object ref = datasource_;
            if (ref instanceof String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8((String) ref);
                datasource_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        // required string datatype = 2;
        public static final int DATATYPE_FIELD_NUMBER = 2;
        private Object datatype_;
        public boolean hasDatatype() {
            return ((bitField0_ & 0x00000002) == 0x00000002);
        }
        public String getDatatype() {
            Object ref = datatype_;
            if (ref instanceof String) {
                return (String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                String s = bs.toStringUtf8();
                if (com.google.protobuf.Internal.isValidUtf8(bs)) {
                    datatype_ = s;
                }
                return s;
            }
        }
        private com.google.protobuf.ByteString getDatatypeBytes() {
            Object ref = datatype_;
            if (ref instanceof String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8((String) ref);
                datatype_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        // required uint32 count = 3;
        public static final int COUNT_FIELD_NUMBER = 3;
        private int count_;
        public boolean hasCount() {
            return ((bitField0_ & 0x00000004) == 0x00000004);
        }
        public int getCount() {
            return count_;
        }

        // optional uint32 datadate = 4;
        public static final int DATADATE_FIELD_NUMBER = 4;
        private int datadate_;
        public boolean hasDatadate() {
            return ((bitField0_ & 0x00000008) == 0x00000008);
        }
        public int getDatadate() {
            return datadate_;
        }

        // optional uint32 createdate = 5;
        public static final int CREATEDATE_FIELD_NUMBER = 5;
        private int createdate_;
        public boolean hasCreatedate() {
            return ((bitField0_ & 0x00000010) == 0x00000010);
        }
        public int getCreatedate() {
            return createdate_;
        }

        // optional string datatypeEx = 6;
        public static final int DATATYPEEX_FIELD_NUMBER = 6;
        private Object datatypeEx_;
        public boolean hasDatatypeEx() {
            return ((bitField0_ & 0x00000020) == 0x00000020);
        }
        public String getDatatypeEx() {
            Object ref = datatypeEx_;
            if (ref instanceof String) {
                return (String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                String s = bs.toStringUtf8();
                if (com.google.protobuf.Internal.isValidUtf8(bs)) {
                    datatypeEx_ = s;
                }
                return s;
            }
        }
        private com.google.protobuf.ByteString getDatatypeExBytes() {
            Object ref = datatypeEx_;
            if (ref instanceof String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8((String) ref);
                datatypeEx_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private void initFields() {
            datasource_ = "";
            datatype_ = "";
            count_ = 0;
            datadate_ = 0;
            createdate_ = 0;
            datatypeEx_ = "";
        }
        private byte memoizedIsInitialized = -1;
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized != -1) return isInitialized == 1;

            if (!hasDatasource()) {
                memoizedIsInitialized = 0;
                return false;
            }
            if (!hasDatatype()) {
                memoizedIsInitialized = 0;
                return false;
            }
            if (!hasCount()) {
                memoizedIsInitialized = 0;
                return false;
            }
            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            getSerializedSize();
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                output.writeBytes(1, getDatasourceBytes());
            }
            if (((bitField0_ & 0x00000002) == 0x00000002)) {
                output.writeBytes(2, getDatatypeBytes());
            }
            if (((bitField0_ & 0x00000004) == 0x00000004)) {
                output.writeUInt32(3, count_);
            }
            if (((bitField0_ & 0x00000008) == 0x00000008)) {
                output.writeUInt32(4, datadate_);
            }
            if (((bitField0_ & 0x00000010) == 0x00000010)) {
                output.writeUInt32(5, createdate_);
            }
            if (((bitField0_ & 0x00000020) == 0x00000020)) {
                output.writeBytes(6, getDatatypeExBytes());
            }
            getUnknownFields().writeTo(output);
        }

        private int memoizedSerializedSize = -1;
        public int getSerializedSize() {
            int size = memoizedSerializedSize;
            if (size != -1) return size;

            size = 0;
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeBytesSize(1, getDatasourceBytes());
            }
            if (((bitField0_ & 0x00000002) == 0x00000002)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeBytesSize(2, getDatatypeBytes());
            }
            if (((bitField0_ & 0x00000004) == 0x00000004)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt32Size(3, count_);
            }
            if (((bitField0_ & 0x00000008) == 0x00000008)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt32Size(4, datadate_);
            }
            if (((bitField0_ & 0x00000010) == 0x00000010)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeUInt32Size(5, createdate_);
            }
            if (((bitField0_ & 0x00000020) == 0x00000020)) {
                size += com.google.protobuf.CodedOutputStream
                        .computeBytesSize(6, getDatatypeExBytes());
            }
            size += getUnknownFields().getSerializedSize();
            memoizedSerializedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;
        @Override
        protected Object writeReplace()
                throws java.io.ObjectStreamException {
            return super.writeReplace();
        }

        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data, extensionRegistry)
                    .buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return newBuilder().mergeFrom(data, extensionRegistry)
                    .buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input, extensionRegistry)
                    .buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            Builder builder = newBuilder();
            if (builder.mergeDelimitedFrom(input)) {
                return builder.buildParsed();
            } else {
                return null;
            }
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            Builder builder = newBuilder();
            if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
                return builder.buildParsed();
            } else {
                return null;
            }
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input).buildParsed();
        }
        public static com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return newBuilder().mergeFrom(input, extensionRegistry)
                    .buildParsed();
        }

        public static Builder newBuilder() { return Builder.create(); }
        public Builder newBuilderForType() { return newBuilder(); }
        public static Builder newBuilder(com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute prototype) {
            return newBuilder().mergeFrom(prototype);
        }
        public Builder toBuilder() { return newBuilder(this); }

        @Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }
        public static final class Builder extends
                com.google.protobuf.GeneratedMessage.Builder<Builder>
                implements com.synway.flinkodps.odpsupload.kafka.RecordProbuf.AttributeOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_Attribute_descriptor;
            }

            protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.internal_static_com_synway_standardizedataplatform_kafka_Attribute_fieldAccessorTable;
            }

            // Construct using com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }
            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
                }
            }
            private static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                datasource_ = "";
                bitField0_ = (bitField0_ & ~0x00000001);
                datatype_ = "";
                bitField0_ = (bitField0_ & ~0x00000002);
                count_ = 0;
                bitField0_ = (bitField0_ & ~0x00000004);
                datadate_ = 0;
                bitField0_ = (bitField0_ & ~0x00000008);
                createdate_ = 0;
                bitField0_ = (bitField0_ & ~0x00000010);
                datatypeEx_ = "";
                bitField0_ = (bitField0_ & ~0x00000020);
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDescriptor();
            }

            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute getDefaultInstanceForType() {
                return com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDefaultInstance();
            }

            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute build() {
                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            private com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute buildParsed()
                    throws com.google.protobuf.InvalidProtocolBufferException {
                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(
                            result).asInvalidProtocolBufferException();
                }
                return result;
            }

            public com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute buildPartial() {
                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute result = new com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                    to_bitField0_ |= 0x00000001;
                }
                result.datasource_ = datasource_;
                if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
                    to_bitField0_ |= 0x00000002;
                }
                result.datatype_ = datatype_;
                if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
                    to_bitField0_ |= 0x00000004;
                }
                result.count_ = count_;
                if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
                    to_bitField0_ |= 0x00000008;
                }
                result.datadate_ = datadate_;
                if (((from_bitField0_ & 0x00000010) == 0x00000010)) {
                    to_bitField0_ |= 0x00000010;
                }
                result.createdate_ = createdate_;
                if (((from_bitField0_ & 0x00000020) == 0x00000020)) {
                    to_bitField0_ |= 0x00000020;
                }
                result.datatypeEx_ = datatypeEx_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute) {
                    return mergeFrom((com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute)other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute other) {
                if (other == com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.getDefaultInstance()) return this;
                if (other.hasDatasource()) {
                    setDatasource(other.getDatasource());
                }
                if (other.hasDatatype()) {
                    setDatatype(other.getDatatype());
                }
                if (other.hasCount()) {
                    setCount(other.getCount());
                }
                if (other.hasDatadate()) {
                    setDatadate(other.getDatadate());
                }
                if (other.hasCreatedate()) {
                    setCreatedate(other.getCreatedate());
                }
                if (other.hasDatatypeEx()) {
                    setDatatypeEx(other.getDatatypeEx());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                if (!hasDatasource()) {

                    return false;
                }
                if (!hasDatatype()) {

                    return false;
                }
                if (!hasCount()) {

                    return false;
                }
                return true;
            }

            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                        com.google.protobuf.UnknownFieldSet.newBuilder(
                                this.getUnknownFields());
                while (true) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            this.setUnknownFields(unknownFields.build());
                            onChanged();
                            return this;
                        default: {
                            if (!parseUnknownField(input, unknownFields,
                                    extensionRegistry, tag)) {
                                this.setUnknownFields(unknownFields.build());
                                onChanged();
                                return this;
                            }
                            break;
                        }
                        case 10: {
                            bitField0_ |= 0x00000001;
                            datasource_ = input.readBytes();
                            break;
                        }
                        case 18: {
                            bitField0_ |= 0x00000002;
                            datatype_ = input.readBytes();
                            break;
                        }
                        case 24: {
                            bitField0_ |= 0x00000004;
                            count_ = input.readUInt32();
                            break;
                        }
                        case 32: {
                            bitField0_ |= 0x00000008;
                            datadate_ = input.readUInt32();
                            break;
                        }
                        case 40: {
                            bitField0_ |= 0x00000010;
                            createdate_ = input.readUInt32();
                            break;
                        }
                        case 50: {
                            bitField0_ |= 0x00000020;
                            datatypeEx_ = input.readBytes();
                            break;
                        }
                    }
                }
            }

            private int bitField0_;

            // required string datasource = 1;
            private Object datasource_ = "";
            public boolean hasDatasource() {
                return ((bitField0_ & 0x00000001) == 0x00000001);
            }
            public String getDatasource() {
                Object ref = datasource_;
                if (!(ref instanceof String)) {
                    String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
                    datasource_ = s;
                    return s;
                } else {
                    return (String) ref;
                }
            }
            public Builder setDatasource(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000001;
                datasource_ = value;
                onChanged();
                return this;
            }
            public Builder clearDatasource() {
                bitField0_ = (bitField0_ & ~0x00000001);
                datasource_ = getDefaultInstance().getDatasource();
                onChanged();
                return this;
            }
            void setDatasource(com.google.protobuf.ByteString value) {
                bitField0_ |= 0x00000001;
                datasource_ = value;
                onChanged();
            }

            // required string datatype = 2;
            private Object datatype_ = "";
            public boolean hasDatatype() {
                return ((bitField0_ & 0x00000002) == 0x00000002);
            }
            public String getDatatype() {
                Object ref = datatype_;
                if (!(ref instanceof String)) {
                    String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
                    datatype_ = s;
                    return s;
                } else {
                    return (String) ref;
                }
            }
            public Builder setDatatype(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000002;
                datatype_ = value;
                onChanged();
                return this;
            }
            public Builder clearDatatype() {
                bitField0_ = (bitField0_ & ~0x00000002);
                datatype_ = getDefaultInstance().getDatatype();
                onChanged();
                return this;
            }
            void setDatatype(com.google.protobuf.ByteString value) {
                bitField0_ |= 0x00000002;
                datatype_ = value;
                onChanged();
            }

            // required uint32 count = 3;
            private int count_ ;
            public boolean hasCount() {
                return ((bitField0_ & 0x00000004) == 0x00000004);
            }
            public int getCount() {
                return count_;
            }
            public Builder setCount(int value) {
                bitField0_ |= 0x00000004;
                count_ = value;
                onChanged();
                return this;
            }
            public Builder clearCount() {
                bitField0_ = (bitField0_ & ~0x00000004);
                count_ = 0;
                onChanged();
                return this;
            }

            // optional uint32 datadate = 4;
            private int datadate_ ;
            public boolean hasDatadate() {
                return ((bitField0_ & 0x00000008) == 0x00000008);
            }
            public int getDatadate() {
                return datadate_;
            }
            public Builder setDatadate(int value) {
                bitField0_ |= 0x00000008;
                datadate_ = value;
                onChanged();
                return this;
            }
            public Builder clearDatadate() {
                bitField0_ = (bitField0_ & ~0x00000008);
                datadate_ = 0;
                onChanged();
                return this;
            }

            // optional uint32 createdate = 5;
            private int createdate_ ;
            public boolean hasCreatedate() {
                return ((bitField0_ & 0x00000010) == 0x00000010);
            }
            public int getCreatedate() {
                return createdate_;
            }
            public Builder setCreatedate(int value) {
                bitField0_ |= 0x00000010;
                createdate_ = value;
                onChanged();
                return this;
            }
            public Builder clearCreatedate() {
                bitField0_ = (bitField0_ & ~0x00000010);
                createdate_ = 0;
                onChanged();
                return this;
            }

            // optional string datatypeEx = 6;
            private Object datatypeEx_ = "";
            public boolean hasDatatypeEx() {
                return ((bitField0_ & 0x00000020) == 0x00000020);
            }
            public String getDatatypeEx() {
                Object ref = datatypeEx_;
                if (!(ref instanceof String)) {
                    String s = ((com.google.protobuf.ByteString) ref).toStringUtf8();
                    datatypeEx_ = s;
                    return s;
                } else {
                    return (String) ref;
                }
            }
            public Builder setDatatypeEx(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000020;
                datatypeEx_ = value;
                onChanged();
                return this;
            }
            public Builder clearDatatypeEx() {
                bitField0_ = (bitField0_ & ~0x00000020);
                datatypeEx_ = getDefaultInstance().getDatatypeEx();
                onChanged();
                return this;
            }
            void setDatatypeEx(com.google.protobuf.ByteString value) {
                bitField0_ |= 0x00000020;
                datatypeEx_ = value;
                onChanged();
            }

            // @@protoc_insertion_point(builder_scope:com.synway.flinkodps.odpsupload.kafka.Attribute)
        }

        static {
            defaultInstance = new Attribute(true);
            defaultInstance.initFields();
        }

        // @@protoc_insertion_point(class_scope:com.synway.flinkodps.odpsupload.kafka.Attribute)
    }

    private static com.google.protobuf.Descriptors.Descriptor
            internal_static_com_synway_standardizedataplatform_kafka_RecordSet_descriptor;
    private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
            internal_static_com_synway_standardizedataplatform_kafka_RecordSet_fieldAccessorTable;
    private static com.google.protobuf.Descriptors.Descriptor
            internal_static_com_synway_standardizedataplatform_kafka_Attribute_descriptor;
    private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
            internal_static_com_synway_standardizedataplatform_kafka_Attribute_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }
    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;
    static {
        String[] descriptorData = {
                "\n\022RecordProbuf.proto\022(com.synway.standar" +
                        "dizedataplatform.kafka\"_\n\tRecordSet\022B\n\005a" +
                        "ttri\030\001 \002(\01323.com.synway.standardizedatap" +
                        "latform.kafka.Attribute\022\016\n\006record\030\002 \003(\014\"" +
                        "z\n\tAttribute\022\022\n\ndatasource\030\001 \002(\t\022\020\n\010data" +
                        "type\030\002 \002(\t\022\r\n\005count\030\003 \002(\r\022\020\n\010datadate\030\004 " +
                        "\001(\r\022\022\n\ncreatedate\030\005 \001(\r\022\022\n\ndatatypeEx\030\006 " +
                        "\001(\tB\002H\001"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
                new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                    public com.google.protobuf.ExtensionRegistry assignDescriptors(
                            com.google.protobuf.Descriptors.FileDescriptor root) {
                        descriptor = root;
                        internal_static_com_synway_standardizedataplatform_kafka_RecordSet_descriptor =
                                getDescriptor().getMessageTypes().get(0);
                        internal_static_com_synway_standardizedataplatform_kafka_RecordSet_fieldAccessorTable = new
                                com.google.protobuf.GeneratedMessage.FieldAccessorTable(
                                internal_static_com_synway_standardizedataplatform_kafka_RecordSet_descriptor,
                                new String[] { "Attri", "Record", },
                                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet.class,
                                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.RecordSet.Builder.class);
                        internal_static_com_synway_standardizedataplatform_kafka_Attribute_descriptor =
                                getDescriptor().getMessageTypes().get(1);
                        internal_static_com_synway_standardizedataplatform_kafka_Attribute_fieldAccessorTable = new
                                com.google.protobuf.GeneratedMessage.FieldAccessorTable(
                                internal_static_com_synway_standardizedataplatform_kafka_Attribute_descriptor,
                                new String[] { "Datasource", "Datatype", "Count", "Datadate", "Createdate", "DatatypeEx", },
                                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.class,
                                com.synway.flinkodps.odpsupload.kafka.RecordProbuf.Attribute.Builder.class);
                        return null;
                    }
                };
        com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[] {
                        }, assigner);
    }

    // @@protoc_insertion_point(outer_class_scope)
}
