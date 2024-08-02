/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.core.metapath.format;

// class JsonPathFormatter // implements IPathFormatter {
//
// public JsonPathFormatter() {
// }
//
// @NonNull
// protected String getEffectiveName(IDefinitionPathSegment segment) {
// return segment.getInstance().getJsonName();
// }
//
// @SuppressWarnings("null")
// @Override
// public String format(IPathSegment segment) {
// return segment.getPathStream().map(pathSegment -> {
// return pathSegment.format(this);
// }).collect(Collectors.joining("."));
// }
//
// @Override
// public @NonNull String formatPathSegment(@NonNull IDocumentPathSegment
// segment) {
// return "$";
// }
//
// @Override
// public String formatPathSegment(IFlagPathSegment segment) {
// return getEffectiveName(segment);
// }
//
// @Override
// public String formatPathSegment(IFieldPathSegment segment) {
// IFieldInstance fieldInstance = segment.getInstance();
//
// String retval;
// switch (fieldInstance.getJsonGroupAsBehavior()) {
// case KEYED:
// // use the identifier to index the map
// INodeItem node = segment.getNodeItem();
// retval = "*";
// if (node != null) {
// IFieldDefinition fieldDefinition = fieldInstance.getDefinition();
// IFlagInstance jsonKeyFlagInstance = fieldDefinition.getJsonKeyFlagInstance();
// if (jsonKeyFlagInstance != null) {
// String keyFlagName = jsonKeyFlagInstance.getEffectiveName();
// IRequiredValueFlagNodeItem flagNode = node.getFlagByName(keyFlagName);
// if (flagNode != null) {
// retval = flagNode.toAtomicItem().asString();
// }
// }
// }
// break;
// case LIST:
// break;
// case NONE:
// break;
// case SINGLETON_OR_LIST:
// break;
// default:
// break;
// }
// return formatModelPathSegment(segment);
// }
//
// @SuppressWarnings("null")
// @Override
// public String formatPathSegment(IAssemblyPathSegment segment) {
// String retval;
// if (segment instanceof IRootAssemblyPathSegment) {
// StringBuilder builder = new StringBuilder();
// builder.append(getEffectiveName(segment));
// retval = builder.toString();
// } else {
// // TODO: does it make sense to use this for an intermediate that has no
// parent?
// retval = formatModelPathSegment(segment);
// }
// return retval;
// }
//
// @SuppressWarnings("null")
// @NonNull
// protected String formatModelPathSegment(IModelPositionalPathSegment segment)
// {
// StringBuilder builder = new StringBuilder(getEffectiveName(segment));
// builder.append('[');
// builder.append(segment.getPosition());
// builder.append(']');
// return builder.toString();
// }
// }
